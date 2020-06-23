package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.util.ResourceBundle;

@CommandLine.Command(name = "rm", description = "Remove a registered game", mixinStandardHelpOptions = true)
public class DeleteGameCli implements Runnable {

    @Spec
    private CommandSpec spec;

    @CommandLine.Parameters(description = "Name of the game {e.g. Tennis}", arity = "1")
    private String name;

    @Inject
    @Game
    private ResourceBundle bundle;
    @Inject
    private GameService gameService;

    @Override
    public void run() {
        gameService.deleteGame(name);
        spec.commandLine().getOut().println(bundle.getString(GameBundleKey.DELETE_SUCCESS));
    }
}
