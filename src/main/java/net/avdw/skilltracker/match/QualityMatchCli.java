package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.game.GameTable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Command(name = "quality", description = "Determine the quality of a match", mixinStandardHelpOptions = true)
public class QualityMatchCli implements Runnable {
    @Option(names = {"-g", "--game"}, description = "Game to calculate for", required = true)
    private String game;
    @Inject
    private PlayerRankingMapBuilder gameMatchTeamBuilder;
    @Inject
    private GameService gameService;
    private Gson gson = new Gson();
    @Inject
    private MatchDataBuilder matchDataBuilder;
    @Inject
    private MatchService matchService;
    @Spec
    private CommandSpec spec;
    @Parameters(arity = "1..*")
    private List<String> teams; // player,player,player
    @Inject
    @Match
    private Templator templator;
    @Inject
    private GroupQualityResolver qualityGroupResolver;

    private void display(final GameTable gameTable, final MatchData matchData, final BigDecimal qualityMetric) {
        String game = gameTable.getName();
        String group = qualityGroupResolver.resolve(qualityMetric.intValue());
        boolean isFreeForAll = teams.size() == 1;
        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.QUALITY_CLI_TITLE,
                gson.fromJson(String.format("{game:'%s',quality:'%s%%',group:'%s'}",
                        game, qualityMetric.intValue(), group), Map.class)));
        if (isFreeForAll) {
            displayFreeForAllDetail(gameTable, matchData);
        } else {
            displayTeamDetail(gameTable, matchData);
        }
    }

    private void displayFreeForAllDetail(final GameTable gameTable, final MatchData matchData) {
        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.QUALITY_FFA_MATCH_TITLE));
        matchData.getTeamDataSet().forEach(team -> {
            StringBuilder stringBuilder = new StringBuilder();
            team.getPlayerTableSet().forEach(playerTable -> {
                MatchTable matchTable = matchService.retrieveLastPlayerMatchForGame(gameTable, playerTable);
                stringBuilder.append(templator.populate(MatchBundleKey.QUALITY_TEAM_PLAYER_ENTRY,
                        gson.fromJson(String.format("{mean:'%s',stdev:'%s',name:'%s'}",
                                matchTable.getMean().setScale(0, RoundingMode.HALF_UP),
                                matchTable.getStandardDeviation().setScale(0, RoundingMode.HALF_UP),
                                playerTable.getName()), Map.class)));
            });
            spec.commandLine().getOut().println(stringBuilder.toString());
        });
    }

    private void displayTeamDetail(final GameTable gameTable, final MatchData matchData) {
        matchData.getTeamDataSet().forEach(team -> {
            AtomicReference<BigDecimal> teamMean = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> teamStdev = new AtomicReference<>(BigDecimal.ZERO);
            StringBuilder stringBuilder = new StringBuilder();
            team.getPlayerTableSet().forEach(playerTable -> {
                MatchTable matchTable = matchService.retrieveLastPlayerMatchForGame(gameTable, playerTable);
                teamMean.set(teamMean.get().add(matchTable.getMean()));
                teamStdev.set(teamStdev.get().add(matchTable.getStandardDeviation()));
                stringBuilder.append(templator.populate(MatchBundleKey.QUALITY_TEAM_PLAYER_ENTRY,
                        gson.fromJson(String.format("{mean:'%s',stdev:'%s',name:'%s'}",
                                matchTable.getMean().setScale(0, RoundingMode.HALF_UP),
                                matchTable.getStandardDeviation().setScale(0, RoundingMode.HALF_UP),
                                playerTable.getName()), Map.class))).append("\n");
            });

            teamMean.set(teamMean.get().divide(BigDecimal.valueOf(team.getPlayerTableSet().size()), RoundingMode.HALF_UP));
            teamStdev.set(teamStdev.get().divide(BigDecimal.valueOf(team.getPlayerTableSet().size()), RoundingMode.HALF_UP));
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.QUALITY_TEAM_TITLE,
                    gson.fromJson(String.format("{mean:'%s',stdev:'%s'}",
                            teamMean.get().setScale(0, RoundingMode.HALF_UP),
                            teamStdev.get().setScale(0, RoundingMode.HALF_UP)),
                            Map.class)));
            spec.commandLine().getOut().println(stringBuilder.toString());
        });
    }

    @Override
    public void run() {
        GameTable gameTable = gameService.retrieveGame(game);
        if (gameTable == null) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.NO_GAME_FOUND));
            return;
        }
        MatchData matchData = matchDataBuilder.buildFromString(teams);
        List<ITeam> teamList = gameMatchTeamBuilder.build(gameTable, matchData);

        BigDecimal qualityMetric = matchService.calculateMatchQuality(gameTable, teamList).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP);

        display(gameTable, matchData, qualityMetric);
    }
}
