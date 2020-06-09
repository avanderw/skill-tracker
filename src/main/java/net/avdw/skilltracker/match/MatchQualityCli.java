package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.BigDecimal;
import java.util.List;
import java.util.ResourceBundle;

@Command(name = "quality", description = "Determine the quality of a match", mixinStandardHelpOptions = true)
public class MatchQualityCli implements Runnable {
    @Spec
    private CommandLine.Model.CommandSpec spec;

    @Parameters
    private List<String> teams; // player,player,player

    @Option(names = {"-g", "--game"}, required = true)
    private String game;

    @Inject
    @Match
    private ResourceBundle bundle;
    @Inject
    private PlayerService playerService;
    @Inject
    private MatchService matchService;
    @Inject
    private MatchMapper matchMapper;
    @Inject
    private MatchCliMapper matchCliMapper;
    @Inject
    private GameService gameService;

    @Override
    public void run() {
        GameTable gameTable = gameService.retrieveGame(game);
        List<ITeam> teamList = matchCliMapper.map(gameTable, teams);
        spec.commandLine().getOut().println(String.format("%,f%%", matchService.calculateMatchQuality(gameTable, teamList).multiply(BigDecimal.valueOf(100))));
    }
}
