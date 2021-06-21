package net.avdw.skilltracker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.SneakyThrows;
import net.avdw.database.LiquibaseRunner;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
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

import static org.junit.Assert.*;

public class GameCliTest {
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

    private static final Path DATABASE_TEST = Paths.get("target/test-resources/game-test.sqlite");
    private CliTester cliTester;
    private CommandLine.IFactory instance;
    private static CommandLine commandLine;
    private static Dao<PlayEntity, Integer> playDao;
    private StringWriter errWriter;
    private StringWriter outWriter;

    @BeforeClass
    public static void beforeClass() throws IOException, SQLException {
        Injector injector = Guice.createInjector(new TestModule("target/test-resources/net.avdw.skilltracker.game/skill-tracker.sqlite"));
        commandLine = new CommandLine(MainCli.class, TestGuiceFactory.getInstance(new TestModule("target/test-resources/net.avdw.skilltracker.game/skill-tracker.sqlite")));

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

        ConnectionSource jdbcConnectionSource = new JdbcConnectionSource(jdbcUrl);
        playDao = DaoManager.createDao(jdbcConnectionSource, PlayEntity.class);

    }

    @SneakyThrows
    @After
    public void afterTest() {
        playDao.delete(playDao.deleteBuilder().prepare());
        instance.create(ConnectionSource.class).close();
    }

    private void assertSuccess(final int exitCode) {
        assertEquals("The command must not have error output", "", errWriter.toString());
        assertNotEquals("The command needs standard output", "", outWriter.toString());
        assertEquals(0, exitCode);
        Logger.debug(outWriter.toString());
    }

    @SneakyThrows
    @Before
    public void beforeTest() {
        Files.createDirectories(DATABASE_TEST.getParent());
        Files.copy(DATABASE_SNAPSHOT, DATABASE_TEST, StandardCopyOption.REPLACE_EXISTING);

        instance = TestGuiceFactory.getInstance(new TestModule(DATABASE_TEST.toString()));
        CommandLine commandLine = new CommandLine(MainCli.class, instance);
        cliTester = new CliTester(commandLine);

        GameCliTest.commandLine = new CommandLine(MainCli.class, instance);
        resetOutput();

        playDao.delete(playDao.deleteBuilder().prepare());
    }

    private void resetOutput() {
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @Test
    public void test_QuotedName() {
        cliTester.execute("game ls").success()
                .notContains("&amp;");
    }

    @Test
    public void testGameEmpty() {
        cliTester.execute("game").success()
                .contains("Usage");
    }


    @Test
    public void testGameNotFound() {
        cliTester.execute("game view random").failure()
                .contains("GameNotFoundException");
    }


    @Test
    public void testListAllGames() {
        cliTester.execute("game ls").success()
                .notContains("Probability")
                .distinct("Unreal Tournament");
    }

    @Test
    public void testListGameFilter() {
        cliTester.execute("game", "ls", "north").success()
                .contains("Northgard");
    }

    @Test
    public void test_ViewDetailMatchListLimit_Pass() {
        assertSuccess(commandLine.execute("match", "add", "Andrew,One", "Jaco,Etienne", "--ranks", "1,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Two", "Jaco,JK", "--ranks", "1,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Three", "Jaco,Marius", "--ranks", "1,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Four", "Jaco,Pieter", "--ranks", "1,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Five", "Jaco,Bot", "--ranks", "1,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Six", "Jaco,Bot", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        assertFalse(outWriter.toString().contains("Andrew & One"));
        assertFalse(outWriter.toString().contains("One & Andrew"));
    }

    @Test
    public void test_ViewGameLimit_Pass() {
        cliTester.execute("game view Northgard").success();
    }

    @Test
    public void testViewGameShortcut() {
        cliTester.execute("game AgeOfEmpires2").success();
    }

    @Test
    public void testViewGame() {
        cliTester.execute("game view UnrealTournament").success()
                .distinct("deba86f7")
                .notContains("#0:")
                .notContains("33.7001678126")
                .notContains("deba86f7-3490-4867-831d-2c9134757def")
                .inOrder("JK", "Zeo", "Andrew", "Karl", "Wicus")
                .inOrder("deba86f7", "caf082b9", "5063f995");
    }

}