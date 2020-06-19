package net.avdw.skilltracker.match;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.inject.Inject;
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

import java.io.StringReader;
import java.io.StringWriter;
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
    @Inject
    private MatchService matchService;
    @Parameters(description = "Players in the game", split = ",", arity = "1", index = "1")
    private List<String> playerList;
    @Inject
    private PlayerRankingMapBuilder playerRankingMapBuilder;
    @Inject
    private PlayerService playerService;
    @Inject
    @Match
    private ResourceBundle resourceBundle;
    @Spec
    private CommandSpec spec;
    @Parameters(description = "Team setup (e.g. 2v1v4)", split = "v", arity = "1", index = "0")
    private List<Integer> teamSize;

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
            spec.commandLine().getOut().println(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH));
            return;
        }

        GameTable gameTable = gameService.retrieveGame(game);
        List<PlayerTable> playerTableList = playerList.stream().map(player -> playerService.createOrRetrievePlayer(player)).collect(Collectors.toList());
        Set<MatchData> matchSet = CollectionUtils.permutations(playerTableList).stream().map(permutations -> formMatch(permutations, teamSize)).collect(Collectors.toSet());
        matchSet.forEach(matchData -> matchData.setQuality(matchService.calculateMatchQuality(gameTable, playerRankingMapBuilder.build(gameTable, matchData))));

        Mustache suggestCliTitleTemplate = new DefaultMustacheFactory().compile(new StringReader(resourceBundle.getString(MatchBundleKey.SUGGEST_CLI_TITLE)), MatchBundleKey.SUGGEST_CLI_TITLE);
        StringWriter suggestCliTitleWriter = new StringWriter();
        suggestCliTitleTemplate.execute(suggestCliTitleWriter, gameTable);
        spec.commandLine().getOut().println(suggestCliTitleWriter.toString());
        playerTableList.forEach(playerTable -> {
            MatchTable matchTable = matchService.retrieveLatestPlayerMatchForGame(gameTable, playerTable);
            spec.commandLine().getOut().println(String.format("> (μ)=%s (σ)=%s \t %s",
                    matchTable.getMean().setScale(2, RoundingMode.HALF_UP),
                    matchTable.getStandardDeviation().setScale(2, RoundingMode.HALF_UP),
                    playerTable.getName()
            ));
        });

        spec.commandLine().getOut().println(String.format("%n%s \t %s",
                resourceBundle.getString(MatchBundleKey.SUGGEST_TABLE_QUALITY_HEADER),
                resourceBundle.getString(MatchBundleKey.SUGGEST_TABLE_TEAM_HEADER)
        ));
        matchSet.stream().sorted(Comparator.comparingDouble((MatchData matchData) -> matchData.getQuality().doubleValue()).reversed()).forEach(matchData ->
                spec.commandLine().getOut().println(String.format("%s%%\t%s",
                        matchData.getQuality().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toString(),
                        matchData.getTeamDataSet().stream().map(teamData -> String.format("(%s)",
                                teamData.getPlayerTableSet().stream().map(PlayerTable::getName).collect(Collectors.joining(", "))))
                                .collect(Collectors.joining(" vs. "))
                        )
                ));
    }
}
