package net.avdw.skilltracker.cli.achievement;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "achievement", description = "Achievement list.", mixinStandardHelpOptions = true,
        subcommands = {
                ComradeCommand.class,
                DominatorCommand.class,
                EnthusiastCommand.class,
                GuardianCommand.class,
                NemesisCommand.class
        })
public class AchievementCli implements Runnable {
    @Spec private CommandSpec spec;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
