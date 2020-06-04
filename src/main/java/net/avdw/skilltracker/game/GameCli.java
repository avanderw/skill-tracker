package net.avdw.skilltracker.game;

import picocli.CommandLine;

@CommandLine.Command(name = "game", description = "Manage game information", mixinStandardHelpOptions = true,
        subcommands = {CreateGameCli.class, DeleteGameCli.class})
public class GameCli implements Runnable {

    @Override
    public void run() {

    }
}
