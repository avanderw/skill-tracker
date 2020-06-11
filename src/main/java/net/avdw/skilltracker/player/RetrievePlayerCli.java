package net.avdw.skilltracker.player;

import com.google.inject.Inject;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.ResourceBundle;

@Command(name = "view", description = "View player information", mixinStandardHelpOptions = true)
public class RetrievePlayerCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Parameters(arity = "1")
    private String name;

    @Inject
    private PlayerService playerService;
    @Inject
    private MatchService matchService;
    @Inject
    @Player
    private ResourceBundle resourceBundle;

    @Override
    public void run() {
        PlayerTable playerTable = playerService.retrievePlayer(name);
        if (playerTable == null) {
            spec.commandLine().getOut().println(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST));
            return;
        }

        List<MatchTable> matchList = matchService.retrieveAllMatchesForPlayer(playerTable);
        matchList.forEach(match -> spec.commandLine().getOut().println(match));
    }
}
