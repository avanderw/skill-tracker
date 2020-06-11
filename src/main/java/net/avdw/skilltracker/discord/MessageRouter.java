package net.avdw.skilltracker.discord;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.tinylog.Logger;

public class MessageRouter {
    private final String prefix;
    private final PingService pingService;
    private final HelpService helpService;
    private final SkillTrackerService skillTrackerService;

    @Inject
    MessageRouter(@Named(DiscordPropertyKey.PREFIX) final String prefix,
                  final PingService pingService,
                  final HelpService helpService,
                  final SkillTrackerService skillTrackerService) {
        this.prefix = prefix;
        this.pingService = pingService;
        this.helpService = helpService;
        this.skillTrackerService = skillTrackerService;
    }

    public void route(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            Logger.trace("Author is bot, doing nothing");
            return;
        }

        if (!event.getMessage().getContentRaw().startsWith(prefix)) {
            Logger.trace("Message does not start with the prefix {}, doing nothing", prefix);
            return;
        }

        String command = event.getMessage().getContentRaw().replaceFirst(prefix, "");
        switch (command) {
            case PingService.COMMAND:
                pingService.processEvent(event);
                break;
            case HelpService.COMMAND:
                helpService.processEvent(event);
                break;
            default:
                skillTrackerService.processEvent(event);
        }
    }
}
