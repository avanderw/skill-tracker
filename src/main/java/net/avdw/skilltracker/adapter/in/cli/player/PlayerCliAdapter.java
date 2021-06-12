package net.avdw.skilltracker.adapter.in.cli.player;

import net.avdw.skilltracker.player.MovePlayerCli;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "player", description = "Player feature", mixinStandardHelpOptions = true,
        subcommands = {ViewPlayerCommand.class, ListPlayerCommand.class, MovePlayerCli.class})
public class PlayerCliAdapter implements Runnable {
    @Spec private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
