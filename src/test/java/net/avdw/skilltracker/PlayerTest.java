package net.avdw.skilltracker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.j256.ormlite.support.ConnectionSource;
import lombok.SneakyThrows;
import net.avdw.database.LiquibaseRunner;
import net.avdw.test.CliTester;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PlayerTest {
    private CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

    private static final Path DATABASE_SNAPSHOT;

    static {
        Path path = null;
        try {
            path = new File(PlayerTest.class.getResource("/database/2021-06-03-new.sqlite").toURI()).toPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        DATABASE_SNAPSHOT = path;
    }

    private static final Path DATABASE_TEST = Paths.get("target/test-resources/player-test.sqlite");
    private CliTester cliTester;
    private CommandLine.IFactory instance;

    @After
    @SneakyThrows
    public void afterTest() {
        instance.create(ConnectionSource.class).close();
        Files.delete(DATABASE_TEST);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.createDirectories(DATABASE_TEST.getParent());
        Files.copy(DATABASE_SNAPSHOT, DATABASE_TEST, StandardCopyOption.REPLACE_EXISTING);

        instance = TestGuiceFactory.getInstance(new TestModule(DATABASE_TEST.toString()));
        CommandLine commandLine = new CommandLine(MainCli.class, instance);
        cliTester = new CliTester(commandLine);

        this.commandLine = new CommandLine(MainCli.class, instance);

        resetOutput();
    }

    @BeforeClass
    public static void beforeClass() throws IOException, SQLException {
        Injector injector = Guice.createInjector(new TestModule("target/test-resources/net.avdw.skilltracker.game/skill-tracker.sqlite"));

        String jdbcUrl = injector.getInstance(Key.get(String.class, Names.named(PropertyName.JDBC_URL)));
        String jdbcPathUrl = jdbcUrl.replace("jdbc:sqlite:", "");
        Path jdbcDirPath = Paths.get(jdbcPathUrl).getParent();
        if (Files.exists(jdbcDirPath)) {
            Files.walk(jdbcDirPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        Files.createDirectories(jdbcDirPath);
        LiquibaseRunner liquibaseRunner = injector.getInstance(LiquibaseRunner.class);
        liquibaseRunner.update();
    }

    private void assertSuccess(final int exitCode) {
        if (!outWriter.toString().isEmpty()) {
            Logger.debug("OUTPUT:\n{}", outWriter.toString());
        }
        if (!errWriter.toString().isEmpty()) {
            Logger.error("ERROR:\n{}", errWriter.toString());
        }
        assertEquals("The command must not have error output", "", errWriter.toString());
        assertNotEquals("The command needs standard output", "", outWriter.toString());
        assertEquals(0, exitCode);
    }

    private int countLinesStartingWith(final String prefix) {
        int count = 0;
        Scanner lineScanner = new Scanner(outWriter.toString());
        while (lineScanner.hasNextLine()) {
            String line = lineScanner.nextLine();
            if (line.startsWith(prefix)) {
                count++;
            }
        }
        return count;
    }

    private void resetOutput() {
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @Test
    public void testEmpty() {
        cliTester.execute("player").success().contains("Usage");
    }

    @Test
    public void testHelp() {
        cliTester.execute("player --help").success();
    }

    @Test
    public void testPlayerListView() {
        cliTester.execute("player ls").success();
    }

    @Test
    public void testPlayerListDetailView() {
        cliTester.execute("player ls -l").success();
    }

    @Test
    public void testCombinePlayer() {
        cliTester.execute("match add CombineA CombineB CombineC --ranks 1,2,2 --game CombineTest").success();
        cliTester.execute("game view CombineTest").success()
                .notContains("#0")
                .contains("#1")
                .contains("(μ)=30,1 (σ)=6,7 CombineA");
        cliTester.execute("player mv CombineA CombineB").success();
        cliTester.execute("game view CombineTest").success()
                .contains("(μ)=26,3 (σ)=6,4 CombineB");
        cliTester.execute("player ls").success()
                .notContains("CombineA");
    }

    @Test
    public void testMovePlayer() {
        cliTester.execute("player view Andrew").success()
                .contains("Andrew has played 7 games over 44 matches");
        cliTester.execute("player mv Andrew Rename").success();
        cliTester.execute("player ls").success()
                .notContains("Andrew")
                .contains("Rename");
        cliTester.execute("player view Rename").success()
                .contains("Rename has played 7 games over 44 matches");
    }

    @Test
    public void testPlayerNotFound() {
        cliTester.execute("player view NotFound").failure()
                .contains("PlayerNotFoundException");
    }


    @Test
    public void testPlayerDetailView() {
        cliTester.execute("player view Andrew").success()
                .contains("7 games")
                .contains("44 matches")
                .contains("Last played: Galaxy Trucker")
                .contains("Top 3 skilled")
                .contains("Top 3 ranked")
                .contains("Most played: Unreal Tournament")
                .contains("Badge");
    }

    @Test
    public void testContestantView() {
        cliTester.execute("player view Andrew -g=UnrealTournament").success()
                .contains("Andrew is #3")
                .contains("24 matches")
                .contains("Last played: 2020-07-09")
                .contains("Skill: 27,2")
                .contains("Nemesis: BOT-Masterful")
                .contains("Minions: BOT-Adept, BOT-Skilled");
    }
}