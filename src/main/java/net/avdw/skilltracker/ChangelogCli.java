package net.avdw.skilltracker;

import com.google.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "changelog", description = "Show last changelog", mixinStandardHelpOptions = true)
public class ChangelogCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Inject
    @SkillTracker
    private Templator templator;

    @Override
    public void run() {
        spec.commandLine().getOut().println(templator.populate(MainBundleKey.LAST_CHANGELOG));
    }
}
