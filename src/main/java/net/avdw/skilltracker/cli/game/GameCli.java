package net.avdw.skilltracker.cli.game;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(name = "game", description = "Game feature.", mixinStandardHelpOptions = true,
        subcommands = {ListGameCommand.class, ViewGameCommand.class})
public class GameCli implements Runnable {
    @Spec private CommandSpec spec;

    @Parameters(arity = "0..1") // cannot force this to 1 as it eats the sub-commands
    private String game;

    @Override
    public void run() {
        if (game == null) {
            spec.commandLine().usage(spec.commandLine().getOut());
        } else {
            spec.commandLine().execute("view", game);
        }
    }
}
