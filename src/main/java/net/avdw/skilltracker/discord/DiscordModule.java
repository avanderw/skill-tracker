package net.avdw.skilltracker.discord;

import com.google.inject.name.Names;
import net.avdw.property.AbstractPropertyModule;
import net.avdw.skilltracker.GuiceFactory;
import net.avdw.skilltracker.MainCli;
import picocli.CommandLine;

import java.util.Properties;

public class DiscordModule extends AbstractPropertyModule {
    @Override
    protected void configure() {
        Properties properties = configureProperties();
        Names.bindProperties(binder(), properties);

        bind(CommandLine.class).toInstance(new CommandLine(MainCli.class, GuiceFactory.getInstance()));
    }

    @Override
    protected Properties defaultProperties() {
        Properties properties = new Properties();
        properties.put(DiscordPropertyKey.API_TOKEN, "default-token");
        properties.put(DiscordPropertyKey.PREFIX, "--");
        return properties;
    }
}
