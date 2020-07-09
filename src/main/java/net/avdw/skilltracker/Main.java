package net.avdw.skilltracker;

import com.google.inject.Key;
import com.google.inject.name.Names;
import lombok.SneakyThrows;
import net.avdw.database.LiquibaseRunner;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main {
    private Main() {
    }

    @SneakyThrows
    private static void createDatabaseIfNotExists() {
        String jdbcUrl = GuiceFactory.INJECTOR.getInstance(Key.get(String.class, Names.named(PropertyName.JDBC_URL)));
        LiquibaseRunner liquibaseRunner = GuiceFactory.INJECTOR.getInstance(LiquibaseRunner.class);

        if (Files.size(Paths.get(jdbcUrl.replace("jdbc:sqlite:", ""))) == 0) {
            liquibaseRunner.update();
        }
    }

    public static void main(final String[] args) {
        createDatabaseIfNotExists();

        CommandLine commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        commandLine.execute(args);
    }
}
