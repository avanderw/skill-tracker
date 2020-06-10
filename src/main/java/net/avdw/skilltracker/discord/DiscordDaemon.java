package net.avdw.skilltracker.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class DiscordDaemon {
    public static void main(String[] args) throws LoginException, InterruptedException {
        // Note: It is important to register your ReadyListener before building
        JDA jda = JDABuilder.createDefault("token")
                .addEventListeners(new GenericListener())
                .setActivity(Activity.listening("!listening"))
                .build();

        // optionally block until JDA is ready
        jda.awaitReady();
    }
}
