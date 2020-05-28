package net.avdw.skilltracker.session;

import picocli.CommandLine.Command;

@Command(name = "session", description = "Some fancy description", version = "1.0-SNAPSHOT", mixinStandardHelpOptions = true,
        subcommands = {CreateSessionCli.class})
public class SessionCli implements Runnable {
    @Override
    public void run() {

    }
}
