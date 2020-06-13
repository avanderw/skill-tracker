package net.avdw.skilltracker.game;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ResourceBundle;

@Command(name = "ls", description = "List registered games", mixinStandardHelpOptions = true)
public class ListGameCli implements Runnable {

    @Parameters(arity = "0..1")
    private String game;

    @Inject
    private GameService gameService;
    @Inject
    @Game
    private ResourceBundle resourceBundle;
    @Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        List<GameTable> gameTableList;
        if (game == null) {
            gameTableList = gameService.retrieveAllGames();
        } else {
            gameTableList = gameService.retrieveGamesLikeName(game);
        }

        if (gameTableList.isEmpty()) {
            spec.commandLine().getOut().println(resourceBundle.getString(GameBundleKey.NO_GAME_FOUND));
        }

        gameTableList.forEach(game->{
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(resourceBundle.getString(GameBundleKey.GAME_TITLE)), GameBundleKey.GAME_TITLE);
            StringWriter stringWriter = new StringWriter();
            mustache.execute(stringWriter, game);
            spec.commandLine().getOut().println(stringWriter.toString());
        });
    }
}
