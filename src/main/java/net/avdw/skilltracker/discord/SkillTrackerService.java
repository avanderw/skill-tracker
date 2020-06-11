package net.avdw.skilltracker.discord;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SkillTrackerService {
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
    private final CommandLine commandLine;
    private final String prefix;

    @Inject
    SkillTrackerService(@Named(DiscordPropertyKey.PREFIX) final String prefix, final CommandLine commandLine) {
        this.prefix = prefix;
        this.commandLine = commandLine;
    }

    public void processEvent(MessageReceivedEvent event) {
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
            event.getChannel().sendMessage(out.toString()).queue();
        }

        if (!err.toString().isEmpty()) {
            event.getChannel().sendFile(err.toString().getBytes(), String.format("error-%s.txt", simpleDateFormat.format(new Date()))).queue();
        }
    }
}
