package net.avdw.skilltracker.player;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.PlayerDbAdapter;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;
import net.avdw.skilltracker.match.MatchService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.Map;

@Command(name = "mv", description = "Change name / combine players", mixinStandardHelpOptions = true)
public class MovePlayerCli implements Runnable {
    private static Gson gson = new Gson();
    @Parameters(description = "Name to change", arity = "1", index = "0")
    private String fromName;
    @Parameters(description = "Name to change to", arity = "1", index = "1")
    private String toName;
    @Inject
    private MatchService matchService;
    @Inject
    private PlayerDbAdapter playerDbAdapter;
    @Spec
    private CommandLine.Model.CommandSpec spec;
    @Inject
    private Templator templator;

    @Override
    public void run() {
        OrmLitePlayer fromPlayer = playerDbAdapter.retrievePlayer(fromName);
        OrmLitePlayer toPlayer = playerDbAdapter.retrievePlayer(toName);

        if (fromPlayer == null) {
            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_NOT_EXIST));
            return;
        }

        if (toPlayer == null) {
            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.CHANGE_NAME,
                    gson.fromJson(String.format("{to:'%s',from:'%s'}",
                            toName, fromPlayer.getName()), Map.class)));
            playerDbAdapter.changeName(fromPlayer, toName);
        } else {
            if (toPlayer.getName().equals(fromPlayer.getName())) {
                spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.REPLACE_SAME_PLAYER,
                        gson.fromJson(String.format("{to:'%s',from:'%s'}",
                                toPlayer.getName(), fromPlayer.getName()), Map.class)));
            } else {
                spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.WARN_REPLACE_PLAYER,
                        gson.fromJson(String.format("{to:'%s',from:'%s'}",
                                toPlayer.getName(), fromPlayer.getName()), Map.class)));
                matchService.combinePlayer(fromPlayer, toPlayer);
            }
        }

    }
}
