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
            if (!sessionIdList.contains(matcher.group(1))) {
                sessionIdList.add(matcher.group(1));
            }
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
    public void test_Create() throws SQLException {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
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
        assertEquals(28.56, andrewSession.getMean().doubleValue(), 0.01);
        assertEquals(23.21, jacoSession.getMean().doubleValue(), 0.01);
        assertEquals(andrewSession.getSessionId(), karlSession.getSessionId());
    }

    @Test
    public void test_BadGameName() {
        assertSuccess(commandLine.execute("match", "quality", "-g=BadName", "One", "Two"));
        assertSuccess(commandLine.execute("match", "suggest", "1v1", "-g=BadName", "Andrew,Karl"));
    }

    @Test
    public void test_CreateExistingPlayer() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        resetOutput();

        assertSuccess(commandLine.execute("match", "add", "Andrew", "Etienne", "Marius,Dean", "--ranks", "1,2,2", "--game", "Northgard"));
    }

    @Test
    public void test_CreateMatchFFS() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "-g=Northgard", "One,Two,Three,Four", "-r=1,2,3,4"));
    }

    @Test
    public void test_CreateRank() {
        assertSuccess(commandLine.execute("game", "add", "UnrealTournament"));
        assertSuccess(commandLine.execute("match", "add", "-g", "UnrealTournament", "-r", "2,1", "JK,BOT-Inhuman", "NikRich,BOT-Inhuman"));
        assertTrue(outWriter.toString().contains("#2:JK & BOT-Inhuman"));
    }

    @Test
    public void test_DeleteBadId() {
        assertSuccess(commandLine.execute("match", "rm", "session-id"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.DELETE_COMMAND_FAILURE)));
    }

    @Test
    public void test_DeleteFirstMatch() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));

        List<String> sessionIdList = getMatchIdList();
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        assertSuccess(commandLine.execute("player", "view", "Karl", "-g=Northgard"));
        assertSuccess(commandLine.execute("match", "rm", sessionIdList.get(0)));
        assertSuccess(commandLine.execute("match", "rm", sessionIdList.get(1)));
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        assertTrue(outWriter.toString().contains("(μ)=31 (σ)=7 Karl"));
        assertTrue(outWriter.toString().contains("(μ)=29 (σ)=8 Karl"));
    }

    @Test
    public void test_DeleteMatch() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "--game", "Northgard", "Andrew,Karl", "Etienne,Jaco", "--ranks", "1,2"));
        List<String> matchIdList = getMatchIdList();

        resetOutput();
        assertSuccess(commandLine.execute("match", "rm", matchIdList.get(0)));
        assertFalse(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.DELETE_COMMAND_FAILURE)));
    }

    @Test
    public void test_DeleteMiddleMatch() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,3", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "First", "Etienne", "Only,Raoul", "--ranks", "1,2,3", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,First", "Marius,Raoul", "--ranks", "1,3,2", "--game", "Northgard"));

        List<String> sessionIdList = getMatchIdList();
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        assertSuccess(commandLine.execute("player", "view", "First", "-g=Northgard"));
        assertSuccess(commandLine.execute("match", "rm", sessionIdList.get(1)));
        assertSuccess(commandLine.execute("player", "view", "First", "-g=Northgard"));
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        assertTrue(outWriter.toString().contains("(μ)=27 (σ)=6 First"));
        assertTrue(outWriter.toString().contains("(μ)=19 (σ)=7 First"));
    }

    @Test
    public void test_DeleteMultipleMatches() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "--game", "Northgard", "Andrew,Karl", "Etienne,Jaco", "--ranks", "1,2"));
        assertSuccess(commandLine.execute("match", "add", "--game", "Northgard", "Andrew,Karl", "Etienne,Jaco", "--ranks", "1,2"));
        List<String> matchIdList = getMatchIdList();

        resetOutput();
        assertSuccess(commandLine.execute("match", "rm", matchIdList.get(0), matchIdList.get(1)));
        assertTrue(outWriter.toString().contains(matchIdList.get(0)));
        assertTrue(outWriter.toString().contains(matchIdList.get(1)));
        assertFalse(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.DELETE_COMMAND_FAILURE)));
    }

    @Test
    public void test_DuplicatePlayer() {
        assertSuccess(commandLine.execute("game", "add", "UnrealTournament"));
        assertSuccess(commandLine.execute("match", "add", "-g", "UnrealTournament", "-r", "2,1,3", "JK,BOT-Inhuman", "NikRich,BOT-Inhuman", "Andrew"));
        assertSuccess(commandLine.execute("player", "view", "BOT-Inhuman", "-g=UnrealTournament"));
        assertTrue(outWriter.toString().contains("(μ) 26 mean skill"));
    }

    @Test
    public void test_Empty() {
        assertSuccess(commandLine.execute("match"));
        assertTrue("Should output usage help", outWriter.toString().contains("Usage"));
    }

    @Test
    public void test_ListLastFewMatches() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "--game", "Northgard", "Andrew,Karl", "Etienne,Jaco", "--ranks", "1,2"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "ls"));
    }

    @Test
    public void test_ListLastFewMatchesEmpty() {
        assertSuccess(commandLine.execute("match", "ls"));
        assertTrue("Should mention no matches", outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.NO_MATCH_FOUND)));
    }

    @Test
    public void test_MatchQuality() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "quality", "Andrew,Karl", "Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("45%"));
    }

    @Test
    public void test_MatchQualityFFA() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "quality", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("9%"));
    }

    @Test
    public void test_MatchSuggest1v1v1() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "1v1v1", "Andrew,Karl,JK", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("20%"));
    }

    @Test
    public void test_NameSensitivityFailure() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "-g=Northgard", "One,one,ONE,onetwo", "-r=1,2,3,4"));
        resetOutput();
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        int count = countLinesStartingWith(">");
        assertEquals(3, count);
    }

    @Test
    public void test_NameSensitivitySuccess() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "-g=Northgard", "One,Two", "-r=1,2"));
        assertSuccess(commandLine.execute("match", "add", "-g=Northgard", "onE,tWo", "-r=1,2"));
        resetOutput();
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        int count = countLinesStartingWith(">");
        assertEquals(4, count);
    }

    @Test
    public void test_TeamCountRankCountMismatch() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_RANK_COUNT_MISMATCH)));
    }

    @Test
    public void test_TeamSuggestFresh() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v2", "Andrew,Karl,Marius,MikeAssassin640", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("45%"));
    }

    @Test
    public void test_TeamSuggestOddTeam() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v1v1", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("12%"));
    }

    @Test
    public void test_TeamSuggestPlaySuggest() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Marius", "Karl,Raoul", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v2", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("50%"));
    }

    @Test
    public void test_TeamSuggestPlayedGame() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v2", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("47%"));
    }

    @Test
    public void test_TeamSuggestPlayerMismatch() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v1", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH)));
        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2v2", "Andrew,Karl,Marius,Raoul,Jaco", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH)));
    }

    @Test
    public void test_TeamSuggestSetup() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "2", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue("Team player mismatch count", outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH)));
    }

    @Test
    public void test_ViewMatchDetail() {
        assertSuccess(commandLine.execute("game", "add", "UnrealTournament"));
        assertSuccess(commandLine.execute("match", "add", "-g", "UnrealTournament", "-r", "2,1,3", "JK,BOT-Inhuman", "NikRich,BOT-Inhuman", "Andrew"));

        String id = getMatchIdList().get(0);
        resetOutput();
        assertSuccess(commandLine.execute("match", "view", id));
    }
}