package net.avdw.skilltracker;

import com.j256.ormlite.support.ConnectionSource;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class Main {
    private Main() {
    }

    @SneakyThrows
    public static void main(final String[] args) {
        GuiceFactory.getInstance().create(DbIntegrity.class).init();

        CommandLine commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        PrintWriter err = new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true);
        commandLine.setOut(out);
        commandLine.setErr(err);
        commandLine.execute(args);
        out.flush();
        err.flush();

        GuiceFactory.getInstance().create(ConnectionSource.class).close();
    }
}
