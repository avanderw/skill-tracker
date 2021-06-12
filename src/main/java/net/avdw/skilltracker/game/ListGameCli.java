package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;

@Command(name = "ls", description = "List registered games", mixinStandardHelpOptions = true)
public class ListGameCli implements Runnable {

    @Parameters(arity = "0..1")
    private String game;

    @Inject
    private GameService gameService;
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        List<OrmLiteGame> ormLiteGameList;
        if (game == null) {
            ormLiteGameList = gameService.retrieveAllGames();
        } else {
            ormLiteGameList = gameService.retrieveGamesLikeName(game);
        }

        if (ormLiteGameList.isEmpty()) {
            spec.commandLine().getOut().println("No games found\n" +
                    "  (use `game add --help` to add a game)\n" +
                    "  (use `game ls` to list all games)");
        }

        ormLiteGameList.forEach(game -> {
            spec.commandLine().getOut().println(game.getName());
        });
    }
}
