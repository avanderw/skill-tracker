package net.avdw.skilltracker;

import net.avdw.skilltracker.game.GameCli;
import net.avdw.skilltracker.player.PlayerCli;
import net.avdw.skilltracker.match.MatchCli;
import org.tinylog.Logger;
import picocli.CommandLine.Command;

@Command(name = "skill-tracker", description = "Some fancy description", version = "1.0-SNAPSHOT", mixinStandardHelpOptions = true,
        subcommands = {
                GameCli.class,
                MatchCli.class,
                PlayerCli.class})
public class MainCli implements Runnable {

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        Logger.debug("MainCli.java entry point. Start coding here");
    }
}
