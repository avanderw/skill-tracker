package net.avdw.skilltracker.discord;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.tinylog.Logger;

public final class DiscordDaemon {
    private static long reconnectTimeout = 1;

    private DiscordDaemon() {

    }

    public static void main(final String[] args) {
        System.setProperty("picocli.ansi", "false");
        Injector injector = Guice.createInjector(new DiscordModule());

        boolean reconnect = true;
        while (reconnect) {
            try {
                // Note: It is important to register your ReadyListener before building
                JDA jda = JDABuilder.createDefault(injector.getInstance(Key.get(String.class, Names.named(DiscordPropertyKey.API_TOKEN))))
                        .addEventListeners(injector.getInstance(GenericListener.class))
                        .setActivity(Activity.of(Activity.ActivityType.WATCHING,
                                String.format("for %shelp", injector.getInstance(Key.get(String.class, Names.named(DiscordPropertyKey.PREFIX))))))
                        .build();

                // optionally block until JDA is ready
                jda.awaitReady();
                reconnect = false;
            } catch (final Exception e) {
                Logger.debug(e);
                reconnect = true;
                if (reconnectTimeout < 128) {
                    reconnectTimeout *= 2;
                }

                try {
                    Logger.info("Exception connecting to discord\n  (likely no connection, retrying in {}s)\n  (enable DEBUG to see more)", reconnectTimeout);
                    Thread.sleep(reconnectTimeout * 1000);
                } catch (InterruptedException ex) {
                    Logger.debug("Thread interrupted, will not reconnect");
                    reconnect = false;
                }
            }
        }
    }

    public static void resetTimeout() {
        reconnectTimeout = 1;
    }
}
