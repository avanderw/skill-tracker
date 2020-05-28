package net.avdw.skilltracker.session;

import net.avdw.skilltracker.GuiceFactory;
import net.avdw.skilltracker.Main;
import net.avdw.skilltracker.MainCli;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class CreateSessionCliTest {
    private static ResourceBundle sessionBundle;
    private StringWriter errWriter;
    private StringWriter outWriter;
    private CommandLine commandLine;


    @BeforeClass
    public static void beforeClass() {
        sessionBundle = ResourceBundle.getBundle("session", Locale.ENGLISH);
    }

    @Before
    public void beforeTest() {
        commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @Test
    public void test_Pass() {
        int exitCode = commandLine.execute("session", "create", "Andrew,Karl", "Jaco,Etienne", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard");

        assertEquals(0, exitCode);
        assertTrue(outWriter.toString().contains(sessionBundle.getString(SessionBundleKey.CREATE_SUCCESS)));
        assertEquals("", errWriter.toString());
    }

    @Test
    public void test_TeamCountRankCountMismatch_Fail() {
        int exitCode = commandLine.execute("session", "create", "Andrew,Karl", "Marius,Raoul", "--ranks", "1,2,2", "--game", "Northgard");

        assertEquals(0, exitCode);
        assertTrue(outWriter.toString().contains(sessionBundle.getString(SessionBundleKey.TEAM_RANK_COUNT_MISMATCH)));
        assertEquals("", errWriter.toString());
    }

}