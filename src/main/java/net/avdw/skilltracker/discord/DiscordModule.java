package net.avdw.skilltracker.discord;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import net.avdw.property.AbstractPropertyModule;
import net.avdw.skilltracker.DbIntegrity;
import net.avdw.skilltracker.GuiceFactory;
import net.avdw.skilltracker.MainCli;
import net.avdw.skilltracker.Templator;
import picocli.CommandLine;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class DiscordModule extends AbstractPropertyModule {
    @Override
    protected void configure() {
        Properties properties = configureProperties();
        Names.bindProperties(binder(), properties);

        bind(CommandLine.class).toInstance(new CommandLine(MainCli.class, GuiceFactory.getInstance()));
        try {
            GuiceFactory.getInstance().create(DbIntegrity.class).init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Properties defaultProperties() {
        Properties properties = new Properties();
        properties.put(DiscordPropertyKey.API_TOKEN, "default-token");
        properties.put(DiscordPropertyKey.PREFIX, "--");
        return properties;
    }

    @Provides
    @Singleton
    @Discord
    ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("discord", Locale.ENGLISH);
    }

    @Provides
    @Singleton
    @Discord
    Templator templatePopulator(@Discord final ResourceBundle resourceBundle) {
        return new Templator(resourceBundle);
    }
}
