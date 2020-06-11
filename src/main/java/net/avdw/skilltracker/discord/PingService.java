package net.avdw.skilltracker.discord;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.tinylog.Logger;

public class PingService {
    public static final String COMMAND = "ping";

    public void processEvent(final MessageReceivedEvent event) {
        Logger.trace("Processing ping event");
        MessageChannel channel = event.getChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong!") /* => RestAction<Message> */
                .queue(response /* => Message */ -> {
                    response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                });

    }
}
