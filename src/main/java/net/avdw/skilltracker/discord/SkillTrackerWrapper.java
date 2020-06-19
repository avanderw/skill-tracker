package net.avdw.skilltracker.discord;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SkillTrackerWrapper {
    private final CommandLine commandLine;
    private final String prefix;

    @Inject
    SkillTrackerWrapper(@Named(DiscordPropertyKey.PREFIX) final String prefix, final CommandLine commandLine) {
        this.prefix = prefix;
        this.commandLine = commandLine;
    }

    public void processEvent(final MessageReceivedEvent event) {
        Logger.trace("Processing skill tracker event");
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();
        commandLine.setOut(new PrintWriter(out));
        commandLine.setErr(new PrintWriter(err));

        String command = event.getMessage().getContentRaw().replaceFirst(prefix, "");
        commandLine.execute(command.split("\\s"));

        if (out.toString().isEmpty() && err.toString().isEmpty()) {
            Logger.debug("Command has no output: {}", command);
        }

        if (!out.toString().isEmpty()) {
            if (out.toString().length() > 2000) {
                event.getChannel().sendFile(out.toString().getBytes(StandardCharsets.UTF_8), String.format("output-%s.txt", new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date()))).queue();
            } else {
                event.getChannel().sendMessage(out.toString()).queue();
            }
        }

        if (!err.toString().isEmpty()) {
            if (err.toString().length() > 2000) {
                event.getChannel().sendFile(err.toString().getBytes(StandardCharsets.UTF_8), String.format("error-%s.txt", new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date()))).queue();
            } else {
                event.getChannel().sendMessage(err.toString()).queue();
            }
        }
    }
}