package net.avdw.skilltracker.player;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ResourceBundle;

@Command(name = "ls", description = "List seen players", mixinStandardHelpOptions = true)
public class ListPlayerCli implements Runnable {
    @Parameters(arity = "0..1")
    private String player;

    @Inject
    private PlayerService playerService;
    @Inject
    @Player
    private ResourceBundle resourceBundle;
    @Spec
    private CommandSpec spec;


    @Override
    public void run() {
        List<PlayerTable> playerTableList;
        if (player == null) {
            playerTableList = playerService.retrieveAllPlayers();
        } else {
            playerTableList = playerService.retrievePlayersLikeName(player);
        }

        if (playerTableList.isEmpty()) {
            spec.commandLine().getOut().println(resourceBundle.getString(PlayerBundleKey.NO_PLAYER_FOUND));
        }

        playerTableList.forEach(playerTable -> {
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(resourceBundle.getString(PlayerBundleKey.PLAYER_TITLE)), PlayerBundleKey.PLAYER_TITLE);
            StringWriter stringWriter = new StringWriter();
            mustache.execute(stringWriter, playerTable);
            spec.commandLine().getOut().println(stringWriter.toString());
        });
    }
}
