package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.ResourceBundle;

@CommandLine.Command(name = "view", description = "View the details of a game", mixinStandardHelpOptions = true)
public class RetrieveGameCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Parameters(arity = "1")
    private String game;

    @Inject
    @Game
    private ResourceBundle resourceBundle;
    @Inject
    private GameService gameService;
    @Inject
    private MatchService matchService;

    @Override
    public void run() {
        GameTable gameTable = gameService.retrieveGame(game);
        List<MatchTable> matchTableList = matchService.retrieveAllMatchesForGame(gameTable);
        matchTableList.forEach(match -> {
            spec.commandLine().getOut().println(match);
        });
    }
}
