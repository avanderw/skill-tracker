package net.avdw.skilltracker.discord;

import com.google.inject.Inject;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

public class HelpService {
    public static final String COMMAND = "help";
    private final CommandLine commandLine;

    @Inject
    HelpService(final CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public void processEvent(final MessageReceivedEvent event) {
        Logger.trace("Processing help event");
        StringWriter out = new StringWriter();
        out.write("```");
        commandLine.usage(new PrintWriter(out));
        out.write("```");
        MessageChannel channel = event.getChannel();
        channel.sendMessage(out.toString()).queue();
    }
}
