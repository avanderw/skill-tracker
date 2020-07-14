package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerService;
import net.avdw.skilltracker.player.PlayerTable;
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
    @Parameters(description = "Players in the game", split = ",", arity = "1", index = "1")
    private List<String> playerList;
    @Inject
    private PlayerRankingMapBuilder playerRankingMapBuilder;
    @Inject
    private PlayerService playerService;
    @Inject
    private QualityGroupResolver qualityGroupResolver;
    @Spec
    private CommandSpec spec;
    @Parameters(description = "Team setup (e.g. 2v1v4)", split = "v", arity = "1", index = "0")
    private List<Integer> teamSize;
    @Inject
    @Match
    private Templator templator;

    private void displayGroup(final Map<String, List<MatchData>> qualityGroupMap,
                              final String groupSummaryBundleKey,
                              final String groupTitleBundleKey) {
        if (qualityGroupMap.get(templator.populate(groupSummaryBundleKey)) == null) {
            return;
        }

        spec.commandLine().getOut().println();
        spec.commandLine().getOut().println(templator.populate(groupTitleBundleKey));
        qualityGroupMap.get(templator.populate(groupSummaryBundleKey)).stream()
                .sorted(Comparator.comparingDouble((MatchData m) -> m.getQuality().doubleValue()).reversed())
                .forEach(m -> spec.commandLine().getOut().println(templator.populate(MatchBundleKey.SUGGEST_LIST_ENTRY,
                        gson.fromJson(String.format("{quality:'%2s%%',setup:'%s'}",
                                m.getQuality().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toString(),
                                m.getTeamDataSet().stream().map(teamData -> String.format("(%s)",
                                        teamData.getPlayerTableSet().stream().map(PlayerTable::getName).collect(Collectors.joining(", "))))
                                        .collect(Collectors.joining(" vs. "))), Map.class))));
    }

    private MatchData formMatch(final List<PlayerTable> nameList, final List<Integer> teamSizeList) {
        MatchData match = new MatchData();
        List<PlayerTable> nameListCopy = new ArrayList<>(nameList);
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
        if (playerList.size() != teamSize.stream().mapToInt(Integer::intValue).sum()) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH));
            return;
        }

        GameTable gameTable = gameService.retrieveGame(game);
        if (gameTable == null) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.NO_GAME_FOUND));
            return;
        }
        List<PlayerTable> playerTableList = playerList.stream().map(player -> playerService.createOrRetrievePlayer(player)).collect(Collectors.toList());
        Set<MatchData> matchSet = CollectionUtils.permutations(playerTableList).stream().map(permutations -> formMatch(permutations, teamSize)).collect(Collectors.toSet());
        matchSet.forEach(matchData -> matchData.setQuality(matchService.calculateMatchQuality(gameTable, playerRankingMapBuilder.build(gameTable, matchData))));

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.SUGGEST_CLI_TITLE,
                gson.fromJson(String.format("{setup:'%s',game:'%s'}",
                        teamSize.stream().map(Object::toString).collect(Collectors.joining("v")),
                        gameTable.getName()), Map.class)));
        playerTableList.forEach(playerTable -> {
            MatchTable matchTable = matchService.retrieveLastPlayerMatchForGame(gameTable, playerTable);
            spec.commandLine().getOut().println(String.format("> (μ)=%2s (σ)=%s \t %s",
                    matchTable.getMean().setScale(0, RoundingMode.HALF_UP),
                    matchTable.getStandardDeviation().setScale(0, RoundingMode.HALF_UP),
                    playerTable.getName()
            ));
        });

        Map<String, List<MatchData>> qualityGroupMap = matchSet.stream().collect(Collectors.groupingBy(m -> qualityGroupResolver.resolve(m.getQuality().multiply(BigDecimal.valueOf(100)).intValue())));
        displayGroup(qualityGroupMap, MatchBundleKey.QUALITY_HI_SUMMARY, MatchBundleKey.SUGGEST_HI_TITLE);
        displayGroup(qualityGroupMap, MatchBundleKey.QUALITY_MED_SUMMARY, MatchBundleKey.SUGGEST_MED_TITLE);
        displayGroup(qualityGroupMap, MatchBundleKey.QUALITY_LOW_SUMMARY, MatchBundleKey.SUGGEST_LOW_TITLE);
        displayGroup(qualityGroupMap, MatchBundleKey.QUALITY_LOWEST_SUMMARY, MatchBundleKey.SUGGEST_LOWEST_TITLE);
    }
}
