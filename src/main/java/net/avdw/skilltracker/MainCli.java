package net.avdw.skilltracker;

import net.avdw.skilltracker.cli.game.GameCli;
import net.avdw.skilltracker.cli.player.PlayerCli;
import net.avdw.skilltracker.match.MatchCli;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "skill-tracker", description = "Player skill tracker for competitive games",
        versionProvider = MainVersion.class, mixinStandardHelpOptions = true,
        subcommands = {
                GameCli.class,
                MatchCli.class,
                PlayerCli.class,
                ChangelogCli.class})
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
