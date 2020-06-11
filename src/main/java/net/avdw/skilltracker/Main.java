package net.avdw.skilltracker;

import picocli.CommandLine;

public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        CommandLine commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        commandLine.execute(args);
    }
}
