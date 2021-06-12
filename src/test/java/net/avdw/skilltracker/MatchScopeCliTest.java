package net.avdw.skilltracker;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.match.MatchBundleKey;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class MatchScopeCliTest {


    private static final Path DATABASE_SNAPSHOT;

    static {
        Path path = null;
        try {
            path = new File(NemesisTest.class.getResource("/database/2021-06-03-new.sqlite").toURI()).toPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        DATABASE_SNAPSHOT = path;
    }

    private static final Path DATABASE_TEST = Paths.get("target/test-resources/database/match-test.sqlite");
    private CliTester cliTester;

    private static CommandLine.IFactory instance;
    private static Dao<PlayEntity, Integer> playDao;
    private static ResourceBundle resourceBundle;
    private CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

    @BeforeClass
    public static void beforeClass() throws SQLException, IOException {
        resourceBundle = ResourceBundle.getBundle("match", Locale.ENGLISH);
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

    @SneakyThrows
    @After
    public void afterTEst() {
        playDao.deleteBuilder().delete();
        instance.create(ConnectionSource.class).close();
    }

    @SneakyThrows
    @Before
    public void beforeTest() throws SQLException {
        Files.createDirectories(DATABASE_TEST.getParent());
        Files.copy(DATABASE_SNAPSHOT, DATABASE_TEST, StandardCopyOption.REPLACE_EXISTING);

        instance = TestGuiceFactory.getInstance(new TestModule(DATABASE_TEST.toString()));
        commandLine = new CommandLine(MainCli.class, instance);
        cliTester = new CliTester(commandLine);
        playDao = DaoManager.createDao(instance.create(ConnectionSource.class), PlayEntity.class);
        resetOutput();
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
    public void test_BadGameName() {
        assertSuccess(commandLine.execute("match", "quality", "-g=BadName", "One", "Two"));
        assertSuccess(commandLine.execute("match", "suggest", "-s=1v1", "-g=BadName", "Three,Four"));
    }

    @Test
    public void testCreate() throws SQLException {
        assertSuccess(commandLine.execute("game", "add", "MatchCreateGame1"));
        resetOutput();

        assertSuccess(commandLine.execute("match", "add", "MatchCreate1,MatchCreate2", "MatchCreate3,MatchCreate4", "MatchCreate5,MatchCreate6", "--ranks", "1,2,2", "--game", "MatchCreateGame1"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.CREATE_SUCCESS)));

        PlayEntity andrew = playDao.queryForFirst(playDao.queryBuilder().where().eq(PlayEntity.PLAYER_NAME, "MatchCreate1").prepare());
        PlayEntity karl = playDao.queryForFirst(playDao.queryBuilder().where().eq(PlayEntity.PLAYER_NAME, "MatchCreate2").prepare());
        PlayEntity jaco = playDao.queryForFirst(playDao.queryBuilder().where().eq(PlayEntity.PLAYER_NAME, "MatchCreate3").prepare());
        assertNotNull(andrew);
        assertNotNull(karl);
        assertNotNull(jaco);

        PlayEntity andrewSession = playDao.queryForFirst(playDao.queryBuilder().where().eq(PlayEntity.PLAYER_NAME, andrew.getPlayerName()).prepare());
        PlayEntity karlSession = playDao.queryForFirst(playDao.queryBuilder().where().eq(PlayEntity.PLAYER_NAME, karl.getPlayerName()).prepare());
        PlayEntity jacoSession = playDao.queryForFirst(playDao.queryBuilder().where().eq(PlayEntity.PLAYER_NAME, jaco.getPlayerName()).prepare());
        assertNotNull(andrewSession);
        assertNotNull(karlSession);
        assertNotNull(jacoSession);
        assertEquals(andrewSession.getPlayerTeam(), karlSession.getPlayerTeam());
        assertNotEquals(andrewSession.getPlayerTeam(), jacoSession.getPlayerTeam());
        assertEquals(Integer.valueOf(1), andrewSession.getTeamRank());
        assertEquals(Integer.valueOf(2), jacoSession.getTeamRank());

        assertNotEquals(25.00, andrewSession.getPlayerMean().doubleValue(), 0.01);
        assertNotEquals(25.00, jacoSession.getPlayerMean().doubleValue(), 0.01);
        assertEquals(28.56, andrewSession.getPlayerMean().doubleValue(), 0.01);
        assertEquals(23.21, jacoSession.getPlayerMean().doubleValue(), 0.01);
        assertEquals(andrewSession.getSessionId(), karlSession.getSessionId());
    }

    @Test
    public void test_CreateBadGame() {
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
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
        assertSuccess(commandLine.execute("match", "add", "-g=Northgard", "One,Two,Three,Four", "--rank=1,2,3,4"));
    }

    @Test
    public void testReverseRank() {
        cliTester.execute("match add -g=UnrealTournament -r 2,1 JK,BOT-Inhuman NikRich,BOT-Inhuman").success()
                .contains("#2:BOT-Inhuman & JK vs. #1:BOT-Inhuman & NikRich");
    }


    @Test
    public void testDuplicateName() {
        cliTester.execute("match add -g=Duplicate -r 1,2 Duplicate1,Duplicate2 Duplicate1,Duplicate2,Name1").success()
                .contains("#1:Duplicate1 & Duplicate2 vs. #2:Duplicate1 & Duplicate2 & Name1");
    }

    @Test
    public void test_DeleteBadId() {
        assertSuccess(commandLine.execute("match", "rm", "session-id"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.DELETE_COMMAND_FAILURE)));
    }

    @Test
    public void test_DeleteFirstMatch() {
        assertSuccess(commandLine.execute("match", "add", "P1,P2", "P3,P4", "P5,P6", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "P1,P2", "P3,P4", "P5,P6", "--ranks", "1,2,2", "--game", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "P1,P2", "P3,P4", "P5,P6", "--ranks", "1,2,2", "--game", "Northgard"));

        List<String> sessionIdList = getMatchIdList();
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        assertSuccess(commandLine.execute("player", "view", "P2", "-g=Northgard"));
        assertSuccess(commandLine.execute("match", "rm", sessionIdList.get(0)));
        assertSuccess(commandLine.execute("match", "rm", sessionIdList.get(1)));
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        assertTrue(outWriter.toString().contains("(μ)=31 (σ)=7 P2"));
        assertTrue(outWriter.toString().contains("(μ)=29 (σ)=8 P2"));
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
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,3", "--game", "N"));
        assertSuccess(commandLine.execute("match", "add", "First", "Etienne", "Only,Raoul", "--ranks", "1,2,3", "--game", "N"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Jaco,First", "Marius,Raoul", "--ranks", "1,3,2", "--game", "N"));

        List<String> sessionIdList = getMatchIdList();
        assertSuccess(commandLine.execute("game", "view", "N"));
        assertSuccess(commandLine.execute("player", "view", "First", "-g=N"));
        assertSuccess(commandLine.execute("match", "rm", sessionIdList.get(1)));
        assertSuccess(commandLine.execute("player", "view", "First", "-g=N"));
        assertSuccess(commandLine.execute("game", "view", "N"));
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
    public void testDuplicatePlayer() {
        cliTester.execute("match", "add", "-g", "N", "-r", "2,1,3", "p1,dup", "p3,dup", "p4").success();
        cliTester.execute("player", "view", "dup", "-g=N").success()
                .contains("1 matches")
                .contains("25,6μ")
                .contains("7,6σ");
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

    @SneakyThrows
    @Test
    public void test_ListLastFewMatchesEmpty() {
        playDao.deleteBuilder().delete();
        cliTester.execute("match ls").success()
                .contains("No matches found");
    }

    @Test
    public void test_MatchQuality() {
        cliTester.execute("match", "quality", "123,234", "345,456", "--game", "Northgard").success()
                .contains("45%");
    }

    @Test
    public void test_MatchQualityFFA() {
        assertSuccess(commandLine.execute("match", "quality", "asdf,sdfg,dfgh,fghj", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("9%"));
    }

    @Test
    public void test_MatchSuggest1v1v1() {
        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "-s=1v1v1", "G,F,K", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("20%"));
    }

    @Test
    public void test_NameSensitivityFailure() {
        assertSuccess(commandLine.execute("match", "add", "-g=N", "One,one,ONE,onetwo", "-r=1,2,3,4"));
        resetOutput();
        assertSuccess(commandLine.execute("game", "view", "N"));
        int count = countLinesStartingWith(">");
        assertEquals(5, count);
    }

    @SneakyThrows
    @Test
    public void test_NameSensitivitySuccess() {
        playDao.deleteBuilder().delete();
        assertSuccess(commandLine.execute("match", "add", "-g=Northgard", "One,Two", "-r=1,2"));
        assertSuccess(commandLine.execute("match", "add", "-g=Northgard", "onE,tWo", "-r=1,2"));
        resetOutput();
        assertSuccess(commandLine.execute("game", "view", "Northgard"));
        int count = countLinesStartingWith(">");
        assertEquals(6, count);
    }

    @Test
    public void test_NvMFromLargePlayerPool() {
        assertSuccess(commandLine.execute("game", "add", "Game"));
        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "-g=Game", "P1,P2,P3,P4", "--setup=1v1"));
        assertSuccess(commandLine.execute("match", "suggest", "-g=Game", "P1,P2,P3,P4,P5", "-s=2v1v1"));
        assertFalse(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH)));
    }

    @Test
    public void test_TeamCountRankCountMismatch() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_RANK_COUNT_MISMATCH)));
    }

    @Test
    public void test_TeamSuggestFresh() {
        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "-s=2v2", "P1,P2,P3,P4", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("45%"));
    }

    @Test
    public void test_TeamSuggestOddTeam() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "P1,P2", "P3,p4", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "-s=2v1v1", "P1,P2,P3,p4", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains("12%"));
    }

    @Test
    public void test_TeamSuggestPlaySuggest() {
        assertSuccess(commandLine.execute("game", "add", "K"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "K"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Marius", "Karl,Raoul", "--ranks", "1,2", "--game", "K"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "-s=2v2", "Andrew,Karl,Marius,Raoul", "--game", "K"));
        assertTrue(outWriter.toString().contains("50%"));
    }

    @Test
    public void test_TeamSuggestPlayedGame() {
        assertSuccess(commandLine.execute("game", "add", "N"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "N"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "-s=2v2", "Andrew,Karl,Marius,Raoul", "--game", "N"));
        assertTrue(outWriter.toString().contains("47%"));
    }

    @Test
    public void test_TeamSuggestPlayerMismatch() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2", "--game", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "--setup=2v1", "Andrew,Karl", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH)));
        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "--setup=2v2", "Andrew,Karl,Marius", "--game", "Northgard"));
        assertTrue(outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.TEAM_PLAYER_COUNT_MISMATCH)));
    }

    @Test
    public void test_TeamSuggestErrorSetup() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));

        resetOutput();
        assertSuccess(commandLine.execute("match", "suggest", "-s=2", "Andrew,Karl,Marius,Raoul", "--game", "Northgard"));
        assertTrue("Team player mismatch count", outWriter.toString().contains(resourceBundle.getString(MatchBundleKey.SUGGEST_SINGLE_TEAM_ERROR)));
    }

    @Test
    public void test_ViewMatchDetail() {
        cliTester.execute("match view 9cacfb0b").success()
                .notContains("#0:");
    }

    @Test
    public void test_createMatchWithWildCard() {
        assertSuccess(commandLine.execute("game", "add", "Northgard"));
        assertSuccess(commandLine.execute("match", "add", "-g=North%", "Andrew", "Karl", "-r=1,2"));
        assertSuccess(commandLine.execute("match", "add", "-g=%rth%", "Andrew", "Karl", "-r=1,2"));
    }
}