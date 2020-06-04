package net.avdw.skilltracker.game;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import net.avdw.database.DbConnection;
import net.avdw.skilltracker.DatabaseModule;
import net.avdw.skilltracker.PropertyName;
import net.avdw.skilltracker.player.PlayerModule;
import net.avdw.skilltracker.session.Session;
import net.avdw.skilltracker.session.SessionModule;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

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
