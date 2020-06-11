package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;

@CommandLine.Command(name = "game", description = "Manage game information", mixinStandardHelpOptions = true,
        subcommands = {ListGameCli.class, CreateGameCli.class, RetrieveGameCli.class, DeleteGameCli.class})
public class GameCli implements Runnable {
    @Parameters(arity = "0..1") // cannot force this to 1 as it eats the sub-commands
    private String game;
    @Inject
    private GameService gameService;
    @Inject
    private MatchService matchService;
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        if (game == null) {
            spec.commandLine().usage(spec.commandLine().getOut());
        } else {
            GameTable gameTable = gameService.retrieveGame(game);
            List<MatchTable> matchTableList = matchService.retrieveAllMatchesForGame(gameTable);
            matchTableList.forEach(match -> {
                spec.commandLine().getOut().println(match);
            });
        }
    }
}
