package net.avdw.skilltracker;

import com.google.inject.name.Names;
import net.avdw.property.AbstractPropertyModule;
import net.avdw.skilltracker.game.GameModule;
import net.avdw.skilltracker.match.MatchModule;
import net.avdw.skilltracker.player.PlayerModule;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

class MainModule extends AbstractPropertyModule {
    @Override
    protected void configure() {
        Properties properties = configureProperties();
        Names.bindProperties(binder(), properties);
        bind(List.class).to(LinkedList.class);

        install(new DatabaseModule());
        install(new GameModule());
        install(new PlayerModule());
        install(new MatchModule());
    }

    @Override
    protected Properties defaultProperties() {
        Properties properties = new Properties();
        properties.put(PropertyName.JDBC_URL, "jdbc:sqlite:skill-tracker.sqlite");
        return properties;
    }
}
