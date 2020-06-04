package net.avdw.skilltracker.game;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import net.avdw.skilltracker.DatabaseModule;
import net.avdw.skilltracker.PropertyName;
import net.avdw.skilltracker.player.PlayerModule;
import net.avdw.skilltracker.session.SessionModule;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        Names.bindProperties(binder(), defaultProperties());
        bind(List.class).to(LinkedList.class);
        install(new DatabaseModule());
        install(new GameModule());
        install(new PlayerModule());
        install(new SessionModule());
    }

    protected Properties defaultProperties() {
        Properties properties = new Properties();
        properties.put(PropertyName.JDBC_URL, "jdbc:sqlite:target/test-resources/net.avdw.skilltracker.game/skill-tracker.sqlite");
        return properties;
    }
}
