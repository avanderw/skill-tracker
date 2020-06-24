package net.avdw.skilltracker.player;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import net.avdw.skilltracker.Templator;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlayerModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    Dao<PlayerTable, Integer> gameDao(final ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, PlayerTable.class);
    }

    @Provides
    @Singleton
    @Player
    ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("player", Locale.ENGLISH);
    }

    @Provides
    @Singleton
    @Player
    Templator templatePopulator(@Player final ResourceBundle resourceBundle) {
        return new Templator(resourceBundle);
    }
}
