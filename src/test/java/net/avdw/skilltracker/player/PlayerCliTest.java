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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (!outWriter.toString().isEmpty()) {
            Logger.debug(outWriter.toString());
        }
        if (!errWriter.toString().isEmpty()) {
            Logger.error(errWriter.toString());
        }
        assertEquals("The command must not have error output", "", errWriter.toString());
        assertNotEquals("The command needs standard output", "", outWriter.toString());
        assertEquals(0, exitCode);
    }

    @Before
    public void beforeTest() throws SQLException {
        commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        resetOutput();

        matchDao.delete(matchDao.deleteBuilder().prepare());
        gameDao.delete(gameDao.deleteBuilder().prepare());
        playerDao.delete(playerDao.deleteBuilder().prepare());
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

    private List<String> getMatchIdList() {
        List<String> sessionIdList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\(([a-z0-9]*)\\)");
        Matcher matcher = pattern.matcher(outWriter.toString());
        while (matcher.find()) {
            sessionIdList.add(matcher.group(1));
        }
        Logger.trace("sessionIdList={}", sessionIdList);
        return sessionIdList;
    }

    private void resetOutput() {
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @Test
    public void test_BadPlayerView() {
        assertSuccess(commandLine.execute("player", "view", "BadName"));
    }

    @Test
    public void test_Empty() {
        assertSuccess(commandLine.execute("player"));
        assertTrue("Should output usage help", outWriter.toString().contains("Usage"));
    }

    @Test
    public void test_ListAll() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("game", "add", "Tooth&Tail"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2", "--game", "Tooth&Tail"));
        resetOutput();

        assertSuccess(commandLine.execute("player", "ls"));
        assertFalse("Should find a player", outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

    @Test
    public void test_ListFilter() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        resetOutput();

        assertSuccess(commandLine.execute("player", "ls", "Andrew"));
        assertFalse("Should find a player", outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

    @Test
    public void test_MoveCombinePlayer() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.1"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("player", "mv", "Andrew", "Jaco"));

        resetOutput();
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        assertSuccess(commandLine.execute("player", "ls"));
        assertEquals(3, countLinesStartingWith(">"));
        assertTrue(outWriter.toString().contains("(μ)=26 (σ)=6 Jaco"));
        assertTrue(outWriter.toString().contains("[ 1] Jaco"));
    }

    @Test
    public void test_MovePlayer() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.1"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("player", "mv", "Andrew", "John"));

        resetOutput();
        assertSuccess(commandLine.execute("player", "ls"));
        assertTrue(outWriter.toString().contains("John"));
    }

    @Test
    public void test_ViewPlayerBasic() {
        assertSuccess(commandLine.execute("player", "view", "Andrew"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

    @Test
    public void test_ViewPlayerDetail() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0.1"));
        assertSuccess(commandLine.execute("game", "add", "AgeOfEmpires", "0.1"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,3", "--game", "AgeOfEmpires"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,3", "--game", "AgeOfEmpires"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,3", "--game", "AgeOfEmpires"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew", "Jaco", "Marius", "--ranks", "1,2,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("player", "view", "Andrew"));
        assertEquals(7, countLinesStartingWith(">"));
        assertFalse(outWriter.toString().contains(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST)));
    }

    @Test
    public void test_ViewPlayerProgression() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "2,1", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("player", "view", "Andrew", "-g=Northgard"));
    }

}