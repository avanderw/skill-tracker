package net.avdw.skilltracker.match;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import de.gesundkrank.jskills.GameInfo;
import net.avdw.database.LiquibaseRunner;
import net.avdw.skilltracker.MainCli;
import net.avdw.skilltracker.PropertyName;
import net.avdw.skilltracker.game.GameTable;
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

public class MatchCliTest {
    private static Dao<GameTable, Integer> gameDao;
    private static Dao<MatchTable, Integer> gamePlayerDao;
    private static GameTable gameTable;
    private static Injector injector;
    private static String jdbcUrl;
    private static Dao<MatchTable, Integer> matchDao;
    private static Dao<PlayerTable, Integer> playerDao;
    private static ResourceBundle sessionBundle;
    private CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

    @BeforeClass
    public static void beforeClass() throws SQLException, IOException {
        injector = Guice.createInjector(new TestModule());
        sessionBundle = ResourceBundle.getBundle("match", Locale.ENGLISH);

        jdbcUrl = injector.getInstance(Key.get(String.class, Names.named(PropertyName.JDBC_URL)));
        String jdbcPathUrl = jdbcUrl.replace("jdbc:sqlite:", "");
        Path jdbcDirPath = Paths.get(jdbcPathUrl).getParent();
        if (Files.exists(jdbcDirPath)) {
            Files.walk(jdbcDirPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            Logger.debug("Deleted {}", jdbcDirPath);
        }
        Files.createDirectories(jdbcDirPath);
        LiquibaseRunner liquibaseRunner = injector.getInstance(LiquibaseRunner.class);
        liquibaseRunner.update();

        ConnectionSource jdbcConnectionSource = new JdbcConnectionSource(jdbcUrl);
        gamePlayerDao = DaoManager.createDao(jdbcConnectionSource, MatchTable.class);
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

        GameInfo g = GameInfo.getDefaultGameInfo();
        gameTable = new GameTable("Northgard", g.getInitialMean(), g.getInitialStandardDeviation(), g.getBeta(), g.getDynamicsFactor(), g.getDrawProbability());
        gameDao.create(gameTable);
    }

    @Test
    public void test_Create_Pass() throws SQLException {
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(sessionBundle.getString(MatchBundleKey.CREATE_SUCCESS)));

        PlayerTable andrew = playerDao.queryForFirst(playerDao.queryBuilder().where().eq(PlayerTable.NAME, "Andrew").prepare());
        PlayerTable karl = playerDao.queryForFirst(playerDao.queryBuilder().where().eq(PlayerTable.NAME, "Karl").prepare());
        PlayerTable jaco = playerDao.queryForFirst(playerDao.queryBuilder().where().eq(PlayerTable.NAME, "Jaco").prepare());
        assertNotNull(andrew);
        assertNotNull(karl);
        assertNotNull(jaco);

        MatchTable andrewSession = gamePlayerDao.queryForFirst(gamePlayerDao.queryBuilder().where().eq(MatchTable.PLAYER_FK, andrew.getPk()).prepare());
        MatchTable karlSession = gamePlayerDao.queryForFirst(gamePlayerDao.queryBuilder().where().eq(MatchTable.PLAYER_FK, karl.getPk()).prepare());
        MatchTable jacoSession = gamePlayerDao.queryForFirst(gamePlayerDao.queryBuilder().where().eq(MatchTable.PLAYER_FK, jaco.getPk()).prepare());
        assertNotNull(andrewSession);
        assertNotNull(karlSession);
        assertNotNull(jacoSession);
        assertEquals(andrewSession.getTeam(), karlSession.getTeam());
        assertNotEquals(andrewSession.getTeam(), jacoSession.getTeam());
        assertEquals(Integer.valueOf(1), andrewSession.getRank());
        assertEquals(Integer.valueOf(2), jacoSession.getRank());

        assertNotEquals(25.00, andrewSession.getMean().doubleValue(), 0.01);
        assertNotEquals(25.00, jacoSession.getMean().doubleValue(), 0.01);
        assertEquals(28.56, andrewSession.getMean().doubleValue(), 0.01);
        assertEquals(23.21, jacoSession.getMean().doubleValue(), 0.01);
        assertEquals(andrewSession.getSessionId(), karlSession.getSessionId());
    }

    @Test
    public void test_Empty_Fail() {
        assertSuccess(commandLine.execute("match"));
        assertTrue("Should output usage help", outWriter.toString().contains("Usage"));
    }

    @Test
    public void test_MatchQuality_Success() {
        assertSuccess(commandLine.execute("match", "quality", "Andrew,Karl", "Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("44.721360%"));
    }

    @Test
    public void test_TeamCountRankCountMismatch_Fail() {
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(sessionBundle.getString(MatchBundleKey.TEAM_RANK_COUNT_MISMATCH)));
    }

}