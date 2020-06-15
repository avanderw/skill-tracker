package net.avdw.skilltracker.game;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.inject.Inject;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@CommandLine.Command(name = "game", description = "Manage game information", mixinStandardHelpOptions = true,
        subcommands = {ListGameCli.class, CreateGameCli.class, RetrieveGameCli.class, DeleteGameCli.class})
public class GameCli implements Runnable {
    @Inject
    @Game
    private ResourceBundle gameBundle;
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
            if (gameTable == null) {
                spec.commandLine().getOut().println(gameBundle.getString(GameBundleKey.NO_GAME_FOUND));
            } else {
                Map<String, List<MatchTable>> matchPlayerMap = matchService.retrieveAllMatchesForGame(gameTable);
                if (matchPlayerMap.isEmpty()) {
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(gameBundle.getString(GameBundleKey.GAME_TITLE)), GameBundleKey.GAME_TITLE);
                    StringWriter stringWriter = new StringWriter();
                    mustache.execute(stringWriter, gameTable);
                    spec.commandLine().getOut().println(stringWriter.toString());
                    spec.commandLine().getOut().println(gameBundle.getString(GameBundleKey.NO_MATCH_FOUND));
                } else {
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(gameBundle.getString(GameBundleKey.GAME_TITLE)), GameBundleKey.GAME_TITLE);
                    StringWriter stringWriter = new StringWriter();
                    mustache.execute(stringWriter, gameTable);
                    spec.commandLine().getOut().println(stringWriter.toString());

                    matchPlayerMap.forEach((key, matchTableList) -> spec.commandLine().getOut().println(key));
                }
            }
        }
    }
}
