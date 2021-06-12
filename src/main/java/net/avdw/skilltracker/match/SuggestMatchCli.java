package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.game.GameService;
import org.apache.commons.collections4.CollectionUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Command(name = "suggest", description = "Calculate quality for every team setup", mixinStandardHelpOptions = true)
public class SuggestMatchCli implements Runnable {
    @Option(names = {"-g", "--game"}, required = true)
    private String game;
    @Inject
    private GameService gameService;
    private Gson gson = new Gson();
    @Inject
    private MatchService matchService;
    @Parameters(description = "Players in the game", split = ",", arity = "1")
    private List<String> playerList;
    @Inject
    private PlayerRankingMapBuilder playerRankingMapBuilder;
    @Inject
    private GroupQualityResolver groupQualityResolver;
    @Spec
    private CommandSpec spec;
    @Option(names = {"-s", "--setup"}, description = "Team setup (e.g. 2v1v4)",
            required = true, split = "v")
    private List<Integer> teamSize;
    @Inject
    @MatchScope
    private Templator templator;

    private void displayGroup(final Map<String, List<MatchData>> groupQualityMap,
                              final String groupSummaryBundleKey,
                              final String groupTitleBundleKey) {
        if (groupQualityMap.get(templator.populate(groupSummaryBundleKey)) == null) {
            return;
        }

        spec.commandLine().getOut().println();
        spec.commandLine().getOut().println(templator.populate(groupTitleBundleKey));
        groupQualityMap.get(templator.populate(groupSummaryBundleKey)).stream()
                .sorted(Comparator.comparingDouble((MatchData m) -> m.getQuality().doubleValue()).reversed())
                .forEach(m -> spec.commandLine().getOut().println(templator.populate(MatchBundleKey.SUGGEST_LIST_ENTRY,
                        gson.fromJson(String.format("{quality:'%2s%%',setup:'%s'}",
                                m.getQuality().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toString(),
                                m.getTeamDataSet().stream().map(teamData -> String.format("(%s)",
                                        teamData.getOrmLitePlayerSet().stream().map(OrmLitePlayer::getName).collect(Collectors.joining(", "))))
                                        .collect(Collectors.joining(" vs. "))), Map.class))));
    }

    private MatchData formMatch(final List<OrmLitePlayer> nameList, final List<Integer> teamSizeList) {
        MatchData match = new MatchData();
        List<OrmLitePlayer> nameListCopy = new ArrayList<>(nameList);
        teamSizeList.forEach(count -> {
            TeamData team = new TeamData();
            for (int i = 0; i < count; i++) {
                team.add(nameListCopy.remove(0));
            }
            match.add(team);
        });
        return match;
    }

    @Override
    public void run() {
        if (playerList.size() < teamSize.stream().mapToInt(Integer::intValue).sum()) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH));
            return;
        }

        if (teamSize.size() == 1) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.SUGGEST_SINGLE_TEAM_ERROR));
            return;
        }

        OrmLiteGame ormLiteGame = gameService.retrieveGame(game);
        if (ormLiteGame == null) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.NO_GAME_FOUND));
            return;
        }

        List<OrmLitePlayer> ormLitePlayerList = playerList.stream().map(OrmLitePlayer::new).collect(Collectors.toList());
        Set<MatchData> matchSet = CollectionUtils.permutations(ormLitePlayerList).stream().map(permutations -> formMatch(permutations, teamSize)).collect(Collectors.toSet());
        matchSet.forEach(matchData -> matchData.setQuality(matchService.calculateMatchQuality(ormLiteGame, playerRankingMapBuilder.build(ormLiteGame, matchData))));

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.SUGGEST_CLI_TITLE,
                gson.fromJson(String.format("{setup:'%s',game:'%s'}",
                        teamSize.stream().map(Object::toString).collect(Collectors.joining("v")),
                        ormLiteGame.getName()), Map.class)));
        ormLitePlayerList.forEach(playerTable -> {
            PlayEntity ormLiteMatch = matchService.retrieveLastPlayerMatchForGame(ormLiteGame, playerTable);
            spec.commandLine().getOut().println(String.format("> (μ)=%2s (σ)=%s \t %s",
                    ormLiteMatch.getPlayerMean().setScale(0, RoundingMode.HALF_UP),
                    ormLiteMatch.getPlayerStdDev().setScale(0, RoundingMode.HALF_UP),
                    playerTable.getName()
            ));
        });

        Map<String, List<MatchData>> qualityGroupMap = matchSet.stream().collect(Collectors.groupingBy(m -> groupQualityResolver.resolve(m.getQuality().multiply(BigDecimal.valueOf(100)).intValue())));
        displayGroup(qualityGroupMap, MatchBundleKey.QUALITY_HI_SUMMARY, MatchBundleKey.SUGGEST_HI_TITLE);
        displayGroup(qualityGroupMap, MatchBundleKey.QUALITY_MED_SUMMARY, MatchBundleKey.SUGGEST_MED_TITLE);
        displayGroup(qualityGroupMap, MatchBundleKey.QUALITY_LOW_SUMMARY, MatchBundleKey.SUGGEST_LOW_TITLE);
        displayGroup(qualityGroupMap, MatchBundleKey.QUALITY_LOWEST_SUMMARY, MatchBundleKey.SUGGEST_LOWEST_TITLE);
    }
}
