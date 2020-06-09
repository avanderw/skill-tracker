package net.avdw.skilltracker.match;

import picocli.CommandLine.Command;

@Command(name = "match", description = "Manage matches and outcomes", version = "1.0-SNAPSHOT", mixinStandardHelpOptions = true,
        subcommands = {CreateMatchCli.class})
public class MatchCli implements Runnable {
    @Override
    public void run() {

    }
}
