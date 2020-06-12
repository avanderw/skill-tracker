package net.avdw.skilltracker;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class MainCliTest {

    private CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

    private void assertSuccess(int exitCode) {
        assertEquals("The command must not have error output", "", errWriter.toString());
        assertNotEquals("The command needs standard output", "", outWriter.toString());
        assertEquals(0, exitCode);
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
    public void test_Empty_Fail() {
        assertSuccess(commandLine.execute());
        assertTrue("Should output usage help", outWriter.toString().contains("Usage"));
    }

}