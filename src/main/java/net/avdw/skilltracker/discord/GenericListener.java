package net.avdw.skilltracker.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.tinylog.Logger;

public class GenericListener implements EventListener {
    @Override
    public void onEvent(GenericEvent genericEvent) {
        Logger.trace("Received event: {}", genericEvent);
        if (genericEvent instanceof ReadyEvent) {
            Logger.trace("API is ready!");
        } else if (genericEvent instanceof MessageReceivedEvent) {
            MessageReceivedEvent event = (MessageReceivedEvent) genericEvent;
            if (event.getAuthor().isBot()) {
                Logger.trace("Author is bot, doing nothing");
                return;
            }

            Message msg = event.getMessage();
            if (msg.getContentRaw().equals("!ping")) {
                MessageChannel channel = event.getChannel();
                long time = System.currentTimeMillis();
                channel.sendMessage("Pong!") /* => RestAction<Message> */
                        .queue(response /* => Message */ -> {
                            response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                        });
            }
        } else {
            Logger.debug("Unhandled event: {}", genericEvent);
        }
    }
}
