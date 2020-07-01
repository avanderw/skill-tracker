package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerService;
import net.avdw.skilltracker.player.PlayerTable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Command(name = "quality", description = "Determine the quality of a match", mixinStandardHelpOptions = true)
public class MatchQualityCli implements Runnable {
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
    @Inject
    private PlayerService playerService;
    @Spec
    private CommandSpec spec;
    @Parameters(arity = "1..*")
    private List<String> teams; // player,player,player
    @Inject
    @Match
    private Templator templator;

    @Override
    public void run() {
        GameTable gameTable = gameService.retrieveGame(game);
        MatchData matchData = matchDataBuilder.buildFromString(teams);
        List<ITeam> teamList = gameMatchTeamBuilder.build(gameTable, matchData);

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.QUALITY_ENTRY,
                gson.fromJson(String.format("{game:'%s',quality:'%s%%'}",
                        gameTable.getName(),
                        matchService.calculateMatchQuality(gameTable, teamList).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP)),
                        Map.class)));
        for (final String team : teams) {
            String t = Arrays.stream(team.split(","))
                    .map(name -> {
                        PlayerTable playerTable = playerService.retrievePlayer(name);
                        MatchTable matchTable = matchService.retrieveLastPlayerMatchForGame(gameTable, playerTable);
                        return templator.populate(MatchBundleKey.QUALITY_TEAM_PLAYER_ENTRY,
                                gson.fromJson(String.format("{mean:'%s',stdev:'%s',name:'%s'}",
                                        matchTable.getMean().setScale(0, RoundingMode.HALF_UP),
                                        matchTable.getStandardDeviation().setScale(0, RoundingMode.HALF_UP),
                                        name),
                                        Map.class));
                    }).collect(Collectors.joining(" & "));

            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.QUALITY_TEAM_PLAYER_TEAM,
                    gson.fromJson(String.format("{team:'%s'}",
                            t),
                            Map.class)));
        }
    }
}
