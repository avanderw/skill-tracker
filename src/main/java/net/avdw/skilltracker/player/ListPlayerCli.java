package net.avdw.skilltracker.player;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.match.MatchService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.Map;

@Command(name = "ls", description = "List all players", mixinStandardHelpOptions = true)
public class ListPlayerCli implements Runnable {
    private Gson gson = new Gson();
    @Inject
    private MatchService matchService;
    @Parameters(arity = "0..1")
    private String player;
    @Inject
    private PlayerService playerService;
    @Spec
    private CommandSpec spec;
    @Inject
    @Player
    private Templator templator;

    @Override
    public void run() {
        List<PlayerTable> playerTableList;
        if (player == null) {
            playerTableList = playerService.retrieveAllPlayers();
        } else {
            playerTableList = playerService.retrievePlayersLikeName(player);
        }

        if (playerTableList.isEmpty()) {
            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_NOT_EXIST));
        }

        spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_LIST_TITLE));
        playerTableList.forEach(playerTable -> spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_TITLE,
                gson.fromJson(String.format("{gameCount:'%s',name:'%s'}",
                        matchService.gameListForPlayer(playerTable).size(),
                        playerTable.getName()), Map.class))));
    }
}
