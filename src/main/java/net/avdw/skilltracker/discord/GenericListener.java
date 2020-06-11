package net.avdw.skilltracker.discord;

import com.google.inject.Inject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.tinylog.Logger;

public class GenericListener implements EventListener {
    private final MessageRouter messageRouter;

    @Inject
    GenericListener(final MessageRouter messageRouter) {
        this.messageRouter = messageRouter;
    }

    @Override
    public void onEvent(GenericEvent genericEvent) {
        Logger.trace("Received event: {}", genericEvent);
        if (genericEvent instanceof ReadyEvent) {
            Logger.trace("API is ready!");
        } else if (genericEvent instanceof MessageReceivedEvent) {
            MessageReceivedEvent event = (MessageReceivedEvent) genericEvent;
            Logger.debug("Raw content: {}", event.getMessage().getContentRaw());
            messageRouter.route(event);
        } else {
            Logger.debug("Unhandled event: {}", genericEvent);
        }
    }
}
