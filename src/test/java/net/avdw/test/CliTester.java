package net.avdw.test;

import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiMode;
import org.fusesource.jansi.AnsiType;
import org.fusesource.jansi.io.AnsiOutputStream;
import org.fusesource.jansi.io.AnsiProcessor;
import org.junit.Assert;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CliTester {
    private final CommandLine commandLine;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    private String all;
    private int exitCode;
    private String[] lastArgs;

    public CliTester(CommandLine  commandLine) {
        this.commandLine = commandLine;
    }

    public CliTester execute(final String command, final String... arguments) {
        out.reset();
        err.reset();
        AnsiOutputStream ansiOutputStream = new AnsiOutputStream(out, new AnsiOutputStream.ZeroWidthSupplier(), AnsiMode.Strip, new AnsiProcessor(out),
                AnsiType.Unsupported, AnsiColors.Colors16, StandardCharsets.UTF_8, () -> {
        }, () -> {
        }, true);
        AnsiOutputStream ansiErrorStream = new AnsiOutputStream(err, new AnsiOutputStream.ZeroWidthSupplier(), AnsiMode.Strip, new AnsiProcessor(err),
                AnsiType.Unsupported, AnsiColors.Colors16, StandardCharsets.UTF_8, () -> {
        }, () -> {
        }, true);

        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.setOut(new PrintWriter(ansiOutputStream, true, StandardCharsets.UTF_8));
        commandLine.setErr(new PrintWriter(ansiErrorStream, true, StandardCharsets.UTF_8));

        if (command == null) {
            exitCode = commandLine.execute();
        } else {
            String[] splitCommand = command.split(" ");
            String[] args;
            if (arguments == null) {
                args = splitCommand;
            } else {
                args = new String[splitCommand.length + arguments.length];
                System.arraycopy(splitCommand, 0, args, 0, splitCommand.length);
                System.arraycopy(arguments, 0, args, splitCommand.length, arguments.length);
            }
            lastArgs = args;
            exitCode = commandLine.execute(args);
        }

        all = "";
        Logger.debug("COMMAND: {}", Arrays.toString(lastArgs));
        if (!out.toString().isEmpty()) {
            all += out.toString();
            Logger.debug("OUTPUT: \n{}", out.toString());
        }
        if (!err.toString().isEmpty()) {
            all += err.toString();
            Logger.error("ERROR: \n{}", err.toString());
        }
        return this;
    }

    public CliTester execute() {
        return execute(null);
    }

    public CliTester execute(final String command) {
        return execute(command, null);
    }

    public CliTester failure() {
        assertFailure(exitCode);
        return this;
    }

    private void assertFailure(final int exitCode) {
        assertNotEquals("MUST HAVE error output", "", err.toString().trim());
        assertEquals("MUST NOT HAVE standard output", "", out.toString().trim());
        assertNotEquals(0, exitCode);
    }

    public CliTester success() {
        assertEquals("MUST NOT HAVE error output", "", err.toString().trim());
        assertNotEquals("MUST HAVE standard output", "", out.toString().trim());
        assertEquals(0, exitCode);
        return this;
    }

    public CliTester contains(final String text) {
        assertTrue(String.format("Output MUST contain '%s'", text), all.contains(text));
        return this;
    }

    public CliTester notContains(final String text) {
        assertFalse(String.format("Output MUST NOT contain '%s'", text), all.contains(text));
        return this;
    }

    public CliTester distinct(String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(all);
        int count = 0;
        while(matcher.find()) {
            count++;
        }
        assertEquals(String.format("Output MUST contain only 1 instance of '%s'", regex), 1, count);
        return this;
    }
}
