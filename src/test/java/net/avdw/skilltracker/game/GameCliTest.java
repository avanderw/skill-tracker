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
import net.avdw.skilltracker.match.MatchTable;
import net.avdw.skilltracker.player.PlayerTable;
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

public class GameCliTest {
    private static CommandLine commandLine;
    private static Dao<GameTable, Integer> gameDao;
    private static Dao<MatchTable, Integer> matchDao;
    private static Dao<PlayerTable, Integer> playerDao;
    private static ResourceBundle resourceBundle;
    private StringWriter errWriter;
    private StringWriter outWriter;

    @BeforeClass
    public static void beforeClass() throws IOException, SQLException {
        Injector injector = Guice.createInjector(new TestModule());
        resourceBundle = ResourceBundle.getBundle("game", Locale.ENGLISH);
        commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());

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
    public void test_QuotedName() {
        assertSuccess(commandLine.execute("game", "add", "Tooth&Tail"));
        assertSuccess(commandLine.execute("game", "ls"));
        assertFalse(outWriter.toString().contains("&amp;"));
    }

    @Test
    public void test_AddGameWithZeroProbability_Fail() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertTrue("Should not allow adding games with 0 probability, causes NaN error on ratings for e.g. Andrew,Karl,Jaco 1,2,2",
                outWriter.toString().contains(resourceBundle.getString(GameBundleKey.NO_ZERO_DRAW_PROBABILITY)));
    }

    @Test
    public void test_CreateDuplicate_Fail() throws SQLException {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        resetOutput();

        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertNotNull(gameDao.queryForEq("name", "Northgard"));
    }

    @Test
    public void test_Create_Pass() throws SQLException {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.2"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(GameBundleKey.ADD_SUCCESS)));
        assertNotNull(gameDao.queryForEq("name", "Northgard"));
    }

    @Test
    public void test_DeleteGameWithMatches_Pass() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("game", "rm", "Northgard"));
        assertSuccess(commandLine.execute("player", "view", "Andrew"));
    }

    @Test
    public void test_BadGameView_Fail() {
        assertSuccess(commandLine.execute("game", "view", "BadGame"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(GameBundleKey.NO_GAME_FOUND)));
    }

    @Test
    public void test_Delete_Fail() {
        assertSuccess(commandLine.execute("game", "rm", "Northgard"));
    }

    @Test
    public void test_Delete_Pass() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        resetOutput();

        assertSuccess(commandLine.execute("game", "rm", "Northgard"));
    }

    @Test
    public void test_Empty_Fail() {
        assertSuccess(commandLine.execute("game"));
        assertTrue("Should output usage help", outWriter.toString().contains("Usage"));
    }

    @Test
    public void test_GameDetail_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.3"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        resetOutput();

        assertSuccess(commandLine.execute("game", "Northgard"));
        assertFalse("Should find the game", outWriter.toString().contains(resourceBundle.getString(GameBundleKey.NO_GAME_FOUND)));
        assertFalse("Should list a single match", outWriter.toString().contains(resourceBundle.getString(GameBundleKey.NO_MATCH_FOUND)));
        assertEquals(2, outWriter.toString().split("\n").length);
    }

    @Test
    public void test_GameNotFound_Fail() {
        assertSuccess(commandLine.execute("game", "random"));
    }

    @Test
    public void test_GameProbabilityOptional_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
    }

    @Test
    public void test_ListAllEmpty_Fail() {
        assertSuccess(commandLine.execute("game", "ls"));
    }

    @Test
    public void test_ListAll_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.1"));
        assertSuccess(commandLine.execute("game", "ls"));

        assertFalse("Should find a game", outWriter.toString().contains(resourceBundle.getString(GameBundleKey.NO_GAME_FOUND)));
    }

    @Test
    public void test_ListFilter_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.1"));
        resetOutput();

        assertSuccess(commandLine.execute("game", "ls", "north"));
        assertFalse("Should find a game", outWriter.toString().contains(resourceBundle.getString(GameBundleKey.NO_GAME_FOUND)));
    }

    @Test
    public void test_ViewDetailMatchListLimit_Pass() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
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
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,One", "Jaco,Etienne", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("game", "view", "Northgard", "--top=1", "--last=2"));
    }

    @Test
    public void test_ViewGameSummary_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.1"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        resetOutput();

        assertSuccess(commandLine.execute("game", "Northgard"));
    }

    @Test
    public void test_ViewGame_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.1"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        resetOutput();

        assertSuccess(commandLine.execute("game", "view", "Northgard"));
    }

}