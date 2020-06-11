package net.avdw.skilltracker.player;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "player", description = "Manage players", version = "1.0-SNAPSHOT", mixinStandardHelpOptions = true,
        subcommands = {RetrievePlayerCli.class})
public class PlayerCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
