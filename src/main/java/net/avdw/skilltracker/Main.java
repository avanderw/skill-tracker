package net.avdw.skilltracker;

import net.avdw.database.LiquibaseRunner;
import org.tinylog.Logger;
import picocli.CommandLine;

public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            LiquibaseRunner liquibaseRunner = GuiceFactory.getInstance().create(LiquibaseRunner.class);
            liquibaseRunner.update();
        } catch (final Exception e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
        }

        CommandLine commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        commandLine.execute(args);
    }
}
