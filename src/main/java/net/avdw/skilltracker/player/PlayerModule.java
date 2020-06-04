package net.avdw.skilltracker.player;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlayerModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(ResourceBundle.class).annotatedWith(Player.class).toInstance(ResourceBundle.getBundle("player", Locale.ENGLISH));

    }

    @Provides
    @Singleton
    Dao<PlayerTable, Integer> gameDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, PlayerTable.class);
    }
}
