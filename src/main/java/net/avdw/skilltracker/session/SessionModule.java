package net.avdw.skilltracker.session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import de.gesundkrank.jskills.SkillCalculator;
import de.gesundkrank.jskills.trueskill.FactorGraphTrueSkillCalculator;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class SessionModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(ResourceBundle.class).annotatedWith(Session.class).toInstance(ResourceBundle.getBundle("session", Locale.ENGLISH));
        bind(SkillCalculator.class).to(FactorGraphTrueSkillCalculator.class).in(Singleton.class);
        bind(SessionMapper.class).toInstance(SessionMapper.INSTANCE);
    }

    @Provides
    @Singleton
    Dao<SessionTable, Integer> gameDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, SessionTable.class);
    }
}
