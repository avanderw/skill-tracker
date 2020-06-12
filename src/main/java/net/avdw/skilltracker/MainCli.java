package net.avdw.skilltracker;

import net.avdw.skilltracker.game.GameCli;
import net.avdw.skilltracker.match.MatchCli;
import net.avdw.skilltracker.player.PlayerCli;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "skill-tracker", description = "Some fancy description", version = "1.0-SNAPSHOT", mixinStandardHelpOptions = true,
        subcommands = {
                GameCli.class,
                MatchCli.class,
                PlayerCli.class})
public class MainCli implements Runnable {
    @Spec
    private CommandSpec spec;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
