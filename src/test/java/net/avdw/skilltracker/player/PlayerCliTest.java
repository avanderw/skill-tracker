package net.avdw.skilltracker.player;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import net.avdw.database.LiquibaseRunner;
import net.avdw.skilltracker.MainCli;
import net.avdw.skilltracker.PropertyName;
import net.avdw.skilltracker.game.GameBundleKey;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.match.MatchTable;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class PlayerCliTest {
    private static Dao<GameTable, Integer> gameDao;
    private static Injector injector;
    private static String jdbcUrl;
    private static Dao<MatchTable, Integer> matchDao;
    private static Dao<PlayerTable, Integer> playerDao;
    private static ResourceBundle resourceBundle;
    private CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

    @BeforeClass
    public static void beforeClass() throws IOException, SQLException {
        injector = Guice.createInjector(new TestModule());
        resourceBundle = ResourceBundle.getBundle("player", Locale.ENGLISH);

        jdbcUrl = injector.getInstance(Key.get(String.class, Names.named(PropertyName.JDBC_URL)));
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
        playerDao = DaoManager.createDao(jdbcConnectionSource, PlayerTable.class);
        gameDao = DaoManager.createDao(jdbcConnectionSource, GameTable.class);
        matchDao = DaoManager.createDao(jdbcConnectionSource, MatchTable.class);
    }

    @After
    public void afterTest() throws SQLException {
        matchDao.delete(matchDao.deleteBuilder().prepare());
        gameDao.delete(gameDao.deleteBuilder().prepare());
        playerDao.delete(playerDao.deleteBuilder().prepare());
    }

    private void assertSuccess(final int exitCode) {
        assertEquals("The command must not have error output", "", errWriter.toString());
        assertNotEquals("The command needs standard output", "", outWriter.toString());
        assertEquals(0, exitCode);
        Logger.debug(outWriter.toString());
    }

    @Before
    public void beforeTest() throws SQLException {
        commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        resetOutput();

        matchDao.delete(matchDao.deleteBuilder().prepare());
        gameDao.delete(gameDao.deleteBuilder().prepare());
        playerDao.delete(playerDao.deleteBuilder().prepare());
    }

    private void resetOutput() {
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @Test
    public void test_Empty_Fail() {
        assertSuccess(commandLine.execute("player"));
        assertTrue("Should output usage help", outWriter.toString().contains("Usage"));
    }

    @Test
    public void test_ListAll_Pass() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        resetOutput();

        assertSuccess(commandLine.execute("player", "ls"));
        assertFalse("Should find a player", outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

    @Test
    public void test_ListFilter_Pass() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        resetOutput();

        assertSuccess(commandLine.execute("player", "ls", "Andrew"));
        assertFalse("Should find a player", outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

    @Test
    public void test_ViewPlayerProgression_Pass() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("player", "view", "Andrew", "-g=Northgard"));
    }

    @Test
    public void test_ViewPlayer_Fail() {
        assertSuccess(commandLine.execute("player", "view", "Andrew"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

    @Test
    public void test_ViewPlayer_Pass() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.1"));
        assertSuccess(commandLine.execute("game", "add", "AgeOfEmpires", "0.1"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,3", "--game", "AgeOfEmpires"));

        resetOutput();
        assertSuccess(commandLine.execute("player", "view", "Andrew"));
        assertFalse(outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

}