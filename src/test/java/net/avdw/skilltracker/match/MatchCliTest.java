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
import org.junit.AfterClass;
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
    private static Injector injector;
    private static ResourceBundle sessionBundle;
    private static String jdbcUrl;
    private static Dao<MatchTable, Integer> gamePlayerDao;
    private static Dao<PlayerTable, Integer> playerDao;
    private static Dao<GameTable, Integer> gameDao;
    private static GameTable gameTable;
    private StringWriter errWriter;
    private StringWriter outWriter;
    private CommandLine commandLine;


    @BeforeClass
    public static void beforeClass() throws SQLException, IOException {
        injector = Guice.createInjector(new TestModule());
        sessionBundle = ResourceBundle.getBundle("session", Locale.ENGLISH);

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
        GameInfo g = GameInfo.getDefaultGameInfo();
        gameTable = new GameTable("Northgard", g.getInitialMean(), g.getInitialStandardDeviation(), g.getBeta(), g.getDynamicsFactor(), g.getDrawProbability());
        gameDao.create(gameTable);
    }

    @AfterClass
    public static void afterClass() throws SQLException {
        gameDao.delete(gameTable);
    }

    @Before
    public void beforeTest() throws SQLException {
        commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));

        playerDao.queryForAll().forEach(playerTable -> {
            try {
                playerDao.delete(playerTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    @Test
    public void test_Create_Pass() throws SQLException {
//        commandLine.execute("session", "create", "Andrew,Marius", "John-Keith,Wicus", "--ranks", "1,2", "--game", "Northgard", "--date", "2020-05-29");
        int exitCode = commandLine.execute("match", "create", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard");
        assertEquals("", errWriter.toString());
        assertTrue(outWriter.toString().contains(sessionBundle.getString(MatchBundleKey.CREATE_SUCCESS)));
        assertEquals(0, exitCode);

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
        assertEquals(new Integer(1), andrewSession.getRank());
        assertEquals(new Integer(2), jacoSession.getRank());

        assertNotEquals(25.00, andrewSession.getMean().doubleValue(), 0.01);
        assertNotEquals(25.00, jacoSession.getMean().doubleValue(), 0.01);
        assertEquals(28.56, andrewSession.getMean().doubleValue(), 0.01);
        assertEquals(23.21, jacoSession.getMean().doubleValue(), 0.01);
        assertEquals(andrewSession.getSessionId(), karlSession.getSessionId());
    }

    @Test
    public void test_TeamCountRankCountMismatch_Fail() throws SQLException {
        int exitCode = commandLine.execute("match", "create", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard");

        assertEquals("", errWriter.toString());
        assertTrue(outWriter.toString().contains(sessionBundle.getString(MatchBundleKey.TEAM_RANK_COUNT_MISMATCH)));
        assertEquals(0, exitCode);
    }

    @Test
    public void test_MatchQuality_Success() {
        int exitCode = commandLine.execute("match", "quality", "Andrew,Karl", "Marius,Raoul", "--game", "Northgard");
        assertEquals("", errWriter.toString());
        assertEquals(0, exitCode);
    }

}