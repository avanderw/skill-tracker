package net.avdw.skilltracker.player;

import picocli.CommandLine.Command;

@Command(name = "player", description = "Manage players", version = "1.0-SNAPSHOT", mixinStandardHelpOptions = true,
        subcommands = {RetrievePlayerCli.class})
public class PlayerCli implements Runnable {

    @Override
    public void run() {

    }
}
