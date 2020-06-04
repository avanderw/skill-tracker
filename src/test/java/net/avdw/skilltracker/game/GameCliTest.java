package net.avdw.skilltracker.game;

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

public class GameCliTest {
    private static Injector injector;
    private static ResourceBundle sessionBundle;
    private static String jdbcUrl;
    private static Dao<GameTable, Integer> gameDao;
    private StringWriter errWriter;
    private StringWriter outWriter;
    private CommandLine commandLine;


    @BeforeClass
    public static void beforeClass() throws IOException, SQLException {
        injector = Guice.createInjector(new TestModule());
        sessionBundle = ResourceBundle.getBundle("game", Locale.ENGLISH);

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
        gameDao = DaoManager.createDao(jdbcConnectionSource, GameTable.class);
    }

    @Before
    public void beforeTest() throws SQLException {
        commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
        gameDao.queryForAll().forEach(tuple -> {
            try {
                gameDao.delete(tuple);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @After
    public void afterTest() throws SQLException {
        gameDao.queryForAll().forEach(tuple -> {
            try {
                gameDao.delete(tuple);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_Create_Pass() throws SQLException {
        int exitCode = commandLine.execute("game", "add", "Northgard", "--draw-probability", "0");

        assertEquals("", errWriter.toString());
        assertTrue(outWriter.toString().contains(sessionBundle.getString(GameBundleKey.ADD_SUCCESS)));
        assertEquals(0, exitCode);

        assertNotNull(gameDao.queryForEq("name", "Northgard"));
    }

    @Test
    public void test_CreateDuplicate_Fail() throws SQLException {
        int exitCode = commandLine.execute("game", "add", "Northgard", "--draw-probability", "0");
        assertEquals(0, exitCode);
        exitCode = commandLine.execute("game", "add", "Northgard", "--draw-probability", "0");
        assertEquals(1, exitCode);

        assertNotNull(gameDao.queryForEq("name", "Northgard"));
    }

    @Test
    public void test_Delete_Pass() throws SQLException {
        gameDao.create(new GameTable("Northgard", 0, 0, 0, 0, 0));
        int exitCode = commandLine.execute("game", "rm", "Northgard");

        assertEquals("", errWriter.toString());
        assertEquals(0, exitCode);
        assertTrue(gameDao.queryForEq("name", "Northgard").isEmpty());
    }

}