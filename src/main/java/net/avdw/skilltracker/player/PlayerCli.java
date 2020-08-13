package net.avdw.skilltracker.player;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "player", description = "Manage players", mixinStandardHelpOptions = true,
        subcommands = {RetrievePlayerCli.class, ListPlayerCli.class, MovePlayerCli.class})
public class PlayerCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
