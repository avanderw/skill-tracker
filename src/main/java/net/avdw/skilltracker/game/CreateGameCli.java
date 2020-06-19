package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import de.gesundkrank.jskills.GameInfo;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.BigDecimal;
import java.util.ResourceBundle;

@Command(name = "add", description = "Add a game which people can create matches against", mixinStandardHelpOptions = true)
class CreateGameCli implements Runnable {
    private static GameInfo defaultGameInfo = GameInfo.getDefaultGameInfo();

    @Spec
    private CommandSpec spec;

    @Parameters(description = "Name of the game {e.g. Tennis}", arity = "1", index = "0")
    private String game;

    @Parameters(description = "Probability of drawing the game {e.g. 0.10 equates to a 10% chance}",
            arity = "0..1", index = "1")
    private BigDecimal drawProbability = BigDecimal.valueOf(defaultGameInfo.getDrawProbability());

    @Inject
    @Game
    private ResourceBundle resourceBundle;
    @Inject
    private GameService gameService;

    @Override
    public void run() {
        if (gameService.retrieveGame(game) == null) {
            gameService.createGame(game, drawProbability);
            spec.commandLine().getOut().println(resourceBundle.getString(GameBundleKey.ADD_SUCCESS));
        } else {
            spec.commandLine().getOut().println(resourceBundle.getString(GameBundleKey.GAME_EXIST_FAILURE));
        }
    }
}
