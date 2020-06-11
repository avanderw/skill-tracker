package net.avdw.skilltracker.discord;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public final class DiscordDaemon {
    private DiscordDaemon() {

    }

    public static void main(final String[] args) throws LoginException, InterruptedException {
        Injector injector = Guice.createInjector(new DiscordModule());
        // Note: It is important to register your ReadyListener before building
        JDA jda = JDABuilder.createDefault(injector.getInstance(Key.get(String.class, Names.named(DiscordPropertyKey.API_TOKEN))))
                .addEventListeners(injector.getInstance(GenericListener.class))
                .setActivity(Activity.of(Activity.ActivityType.WATCHING,
                        String.format("for %shelp", injector.getInstance(Key.get(String.class, Names.named(DiscordPropertyKey.PREFIX))))))
                .build();

        // optionally block until JDA is ready
        jda.awaitReady();
    }
}
