package net.avdw.skilltracker.game;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class GameModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(ResourceBundle.class).annotatedWith(Game.class).toInstance(ResourceBundle.getBundle("game", Locale.ENGLISH));
        bind(GameMapper.class).toInstance(GameMapper.INSTANCE);
    }

    @Provides
    @Singleton
    Dao<GameTable, Integer> gameDao(final ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, GameTable.class);
    }
}
