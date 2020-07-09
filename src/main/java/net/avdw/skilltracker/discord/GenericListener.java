package net.avdw.skilltracker.discord;

import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.tinylog.Logger;

public class GenericListener implements EventListener {
    private final MessageRouter messageRouter;

    private Templator templator;

    @Inject
    GenericListener(final MessageRouter messageRouter, @Discord final Templator templator) {
        this.messageRouter = messageRouter;
        this.templator = templator;
    }

    @Override
    public void onEvent(final GenericEvent genericEvent) {
        Logger.trace("Received event: {}", genericEvent);
        if (genericEvent instanceof ReadyEvent) {
            System.out.println(templator.populate(DiscordBundleKey.READY_EVENT));
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
