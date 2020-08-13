package net.avdw.skilltracker.game;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import net.avdw.skilltracker.DatabaseModule;
import net.avdw.skilltracker.PropertyName;
import net.avdw.skilltracker.SkillTracker;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.match.MatchModule;
import net.avdw.skilltracker.player.PlayerModule;

import java.util.*;

class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        Names.bindProperties(binder(), defaultProperties());
        bind(List.class).to(LinkedList.class);
        install(new DatabaseModule());
        install(new GameModule());
        install(new PlayerModule());
        install(new MatchModule());
    }

    protected Properties defaultProperties() {
        Properties properties = new Properties();
        properties.put(PropertyName.JDBC_URL, "jdbc:sqlite:target/test-resources/net.avdw.skilltracker.game/skill-tracker.sqlite");
        return properties;
    }

    @Provides
    @Singleton
    @SkillTracker
    ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("skilltracker", Locale.ENGLISH);
    }

    @Provides
    @Singleton
    @SkillTracker
    Templator templator(@SkillTracker final ResourceBundle resourceBundle) {
        return new Templator(resourceBundle);
    }
}
