package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import de.gesundkrank.jskills.GameInfo;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.ResourceBundle;

@Command(name = "add", description = "Add a game which people can create matches against", mixinStandardHelpOptions = true)
class CreateGameCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Parameters(description = "Name of the game {e.g. Tennis}", arity = "1")
    private String game;

    @Option(names = "--draw-probability",
            description = "Probability of drawing the game {e.g. 0.10 equates to a 10% chance}",
            required = true)
    private double drawProbability = 0.10;

    @Inject
    @Game
    private ResourceBundle resourceBundle;
    @Inject
    private GameService gameService;

    @Override
    public void run() {
        if (gameService.retrieveGame(game) == null) {
            GameInfo defaultGameInfo = GameInfo.getDefaultGameInfo();
            gameService.createGame(game, defaultGameInfo.getInitialMean(), defaultGameInfo.getInitialStandardDeviation(), defaultGameInfo.getBeta(), defaultGameInfo.getDynamicsFactor(), drawProbability);
            spec.commandLine().getOut().println(resourceBundle.getString(GameBundleKey.ADD_SUCCESS));
        } else {
            spec.commandLine().getOut().println(resourceBundle.getString(GameBundleKey.GAME_EXIST_FAILURE));
        }
    }
}
