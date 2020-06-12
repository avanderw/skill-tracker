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
    ResourceBundle resourceBundle;
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
                spec.commandLine().getOut().println(resourceBundle.getString(GameBundleKey.NO_GAME_FOUND));
            } else {
                List<MatchTable> matchTableList = matchService.retrieveAllMatchesForGame(gameTable);
                if (matchTableList.isEmpty()) {
                    spec.commandLine().getOut().println(resourceBundle.getString(GameBundleKey.NO_MATCH_FOUND));
                } else {
                    matchTableList.forEach(match -> spec.commandLine().getOut().println(match));

                }
            }
        }
    }
}
