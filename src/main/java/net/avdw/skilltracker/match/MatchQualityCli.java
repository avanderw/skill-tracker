package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.game.GameTable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Command(name = "quality", description = "Determine the quality of a match", mixinStandardHelpOptions = true)
public class MatchQualityCli implements Runnable {
    @Option(names = {"-g", "--game"}, required = true)
    private String game;
    @Inject
    private PlayerRankingMapBuilder gameMatchTeamBuilder;
    @Inject
    private GameService gameService;
    @Inject
    private MatchDataBuilder matchDataBuilder;
    @Inject
    private MatchService matchService;
    @Spec
    private CommandLine.Model.CommandSpec spec;
    @Parameters
    private List<String> teams; // player;player;player

    @Override
    public void run() {
        GameTable gameTable = gameService.retrieveGame(game);
        MatchData matchData = matchDataBuilder.build(teams);
        List<ITeam> teamList = gameMatchTeamBuilder.build(gameTable, matchData);
        spec.commandLine().getOut().println(String.format(Locale.ENGLISH, "%,f%%", matchService.calculateMatchQuality(gameTable, teamList).multiply(BigDecimal.valueOf(100))));
    }
}
