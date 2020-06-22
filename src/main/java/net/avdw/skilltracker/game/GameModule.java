package net.avdw.skilltracker.game;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import de.gesundkrank.jskills.GameInfo;
import net.avdw.skilltracker.Templator;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class GameModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(GameMapper.class).toInstance(GameMapper.INSTANCE);
        bind(GameInfo.class).toInstance(GameInfo.getDefaultGameInfo());
    }

    @Provides
    @Singleton
    Dao<GameTable, Integer> gameDao(final ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, GameTable.class);
    }

    @Provides
    @Singleton
    @Game
    ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("game", Locale.ENGLISH);
    }

    @Provides
    @Singleton
    @Game
    Templator templatePopulator(@Game final ResourceBundle resourceBundle) {
        return new Templator(resourceBundle);
    }
}
