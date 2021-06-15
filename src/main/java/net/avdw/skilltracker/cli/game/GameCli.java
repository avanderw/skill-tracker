package net.avdw.skilltracker.cli.game;

import net.avdw.skilltracker.game.CreateGameCli;
import net.avdw.skilltracker.game.DeleteGameCli;
import net.avdw.skilltracker.game.ListGameCli;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(name = "game", description = "Query game statistics", mixinStandardHelpOptions = true,
        subcommands = {ListGameCli.class, CreateGameCli.class, ViewGameCommand.class, DeleteGameCli.class})
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
