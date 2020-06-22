package net.avdw.skilltracker.match;

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
import net.avdw.skilltracker.player.PlayerTable;
import org.junit.*;
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
    private static GameTable gameTable;
    private static Injector injector;
    private static String jdbcUrl;
    private static Dao<MatchTable, Integer> matchDao;
    private static Dao<PlayerTable, Integer> playerDao;
    private static ResourceBundle resourceBundle;
    private CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

    @BeforeClass
    public static void beforeClass() throws SQLException, IOException {
        injector = Guice.createInjector(new TestModule());
        resourceBundle = ResourceBundle.getBundle("match", Locale.ENGLISH);

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
    public void test_CreateExistingPlayer_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        resetOutput();

        assertSuccess(commandLine.execute("match", "add", "Andrew", "Etienne", "Marius,Dean", "--ranks", "1,2,2", "--game", "Northgard"));
    }

    @Test
    public void test_Create_Pass() throws SQLException {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        resetOutput();

        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.CREATE_SUCCESS)));

        PlayerTable andrew = playerDao.queryForFirst(playerDao.queryBuilder().where().eq(PlayerTable.NAME, "Andrew").prepare());
        PlayerTable karl = playerDao.queryForFirst(playerDao.queryBuilder().where().eq(PlayerTable.NAME, "Karl").prepare());
        PlayerTable jaco = playerDao.queryForFirst(playerDao.queryBuilder().where().eq(PlayerTable.NAME, "Jaco").prepare());
        assertNotNull(andrew);
        assertNotNull(karl);
        assertNotNull(jaco);

        MatchTable andrewSession = matchDao.queryForFirst(matchDao.queryBuilder().where().eq(MatchTable.PLAYER_FK, andrew.getPk()).prepare());
        MatchTable karlSession = matchDao.queryForFirst(matchDao.queryBuilder().where().eq(MatchTable.PLAYER_FK, karl.getPk()).prepare());
        MatchTable jacoSession = matchDao.queryForFirst(matchDao.queryBuilder().where().eq(MatchTable.PLAYER_FK, jaco.getPk()).prepare());
        assertNotNull(andrewSession);
        assertNotNull(karlSession);
        assertNotNull(jacoSession);
        assertEquals(andrewSession.getTeam(), karlSession.getTeam());
        assertNotEquals(andrewSession.getTeam(), jacoSession.getTeam());
        assertEquals(Integer.valueOf(1), andrewSession.getRank());
        assertEquals(Integer.valueOf(2), jacoSession.getRank());

        assertNotEquals(25.00, andrewSession.getMean().doubleValue(), 0.01);
        assertNotEquals(25.00, jacoSession.getMean().doubleValue(), 0.01);
        assertEquals(24.75, andrewSession.getMean().doubleValue(), 0.01);
        assertEquals(15.12, jacoSession.getMean().doubleValue(), 0.01);
        assertEquals(andrewSession.getSessionId(), karlSession.getSessionId());
    }

    @Test
    public void test_Empty_Fail() {
        assertSuccess(commandLine.execute("match"));
        assertTrue("Should output usage help", outWriter.toString().contains("Usage"));
    }

    @Test
    public void test_MatchQualityFFA_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertSuccess(commandLine.execute("match", "quality", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("8.944272%"));
    }

    @Test
    public void test_MatchQuality_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertSuccess(commandLine.execute("match", "quality", "Andrew,Karl", "Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("44.721360%"));
    }

    @Test
    public void test_MatchSuggest1v1v1_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "1v1v1", "Andrew,Karl,JK", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("20.00%"));
    }

    @Test
    public void test_TeamCountRankCountMismatch_Fail() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_RANK_COUNT_MISMATCH)));
    }

    @Test
    public void test_TeamSuggestFresh_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v2", "Andrew,Karl,Marius,MikeAssassin640", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("44.72%"));
    }

    @Test
    public void test_TeamSuggestOddTeam_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v1v1", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("11.47%"));
    }

    @Test
    public void test_TeamSuggestPlaySuggest_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Marius", "Karl,Raoul", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v2", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("49.63%"));
    }

    @Test
    public void test_TeamSuggestPlayedGame_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v2", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("47.19%"));
    }

    @Test
    public void test_TeamSuggestPlayerMismatch_Fail() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v1", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH)));
        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v2", "Andrew,Karl,Marius,Raoul,Jaco", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH)));
    }

    @Test
    public void test_TeamSuggestSetup_Fail() {
        assertSuccess(commandLine.execute("game", "add", "Northgard", "0"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue("Team player mismatch count", outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH)));
    }

    @Test
    public void test_ListLastFewMatches_Success() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "--game", "Northgard", "Andrew,Karl", "Etienne,Jaco", "--ranks", "1,2"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "ls"));
    }

    @Ignore
    @Test
    public void test_ListLastFewMatchesEmpty_Success() {
        assertSuccess(commandLine.execute("match", "ls"));
        assertTrue("Should mention no matches", outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.NO_MATCH_FOUND)));
    }

    @Ignore
    @Test
    public void test_DeleteMatch_Success() {
        assertSuccess(commandLine.execute("match", "rm", "session-id"));
    }
}