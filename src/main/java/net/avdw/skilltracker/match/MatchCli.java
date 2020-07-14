package net.avdw.skilltracker.match;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "match", description = "Manage matches and outcomes", version = "1.0-SNAPSHOT", mixinStandardHelpOptions = true,
        subcommands = {ListMatchCli.class, CreateMatchCli.class, RetrieveMatchCli.class, DeleteMatchCli.class, QualityMatchCli.class, SuggestMatchCli.class})
public class MatchCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
