package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import picocli.CommandLine;

import java.util.ResourceBundle;

@CommandLine.Command(name = "rm", description = "Remove a game from the library", mixinStandardHelpOptions = true)
public class DeleteGameCli implements Runnable {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(description = "Name of the game {e.g. Tennis}", arity = "1")
    private String name;

    @Inject
    @Game
    ResourceBundle bundle;
    @Inject
    GameService gameService;

    @Override
    public void run() {
        gameService.deleteGame(name);
        spec.commandLine().getOut().println(bundle.getString(GameBundleKey.DELETE_SUCCESS));
    }
}
