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

@CommandLine.Command(name = "game", description = "Manage game information", mixinStandardHelpOptions = true,
        subcommands = {ListGameCli.class, CreateGameCli.class, RetrieveGameCli.class, DeleteGameCli.class})
public class GameCli implements Runnable {
    @Inject
    @Game
    private ResourceBundle bundle;
    @Parameters(arity = "0..1")
    private String game;
    @Inject
    private GameService gameService;
    @Inject
    private MatchService matchService;
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        GameTable gameTable = gameService.retrieveGame(game);
        List<MatchTable> matchTableList = matchService.retrieveAllMatchesForGame(gameTable);
        matchTableList.forEach(match -> {
            spec.commandLine().getOut().println(match);
        });
    }
}
