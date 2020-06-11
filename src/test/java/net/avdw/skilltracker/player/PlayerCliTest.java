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
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.match.MatchTable;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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

    private void assertSuccess(int exitCode) {
        assertEquals("The command must not have error output", "", errWriter.toString());
        assertNotEquals("The command needs standard output", "", outWriter.toString());
        assertEquals(0, exitCode);
    }

    @Before
    public void beforeTest() throws SQLException {
        commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));

        matchDao.delete(matchDao.deleteBuilder().prepare());
        gameDao.delete(gameDao.deleteBuilder().prepare());
        playerDao.delete(playerDao.deleteBuilder().prepare());
    }

    @Test
    public void test_EmptPlayer_Fail() {
        assertSuccess(commandLine.execute("player"));
        assertTrue("Should output usage help", outWriter.toString().contains("Usage"));
    }

    @Test
    public void test_ViewPlayer_Fail() {
        int exitCode = commandLine.execute("player", "view", "Andrew");

        assertEquals("", errWriter.toString());
        assertEquals(0, exitCode);
        assertTrue(outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

    @Test
    public void test_ViewPlayer_Success() {
        commandLine.execute("game", "add", "Northgard", "--draw-probability", "0");
        commandLine.execute("match", "create", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard");

        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
        int exitCode = commandLine.execute("player", "view", "Andrew");

        assertEquals("", errWriter.toString());
        assertNotEquals("", outWriter.toString());
        assertEquals(0, exitCode);
        assertFalse(outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

}