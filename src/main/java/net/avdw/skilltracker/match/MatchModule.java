package net.avdw.skilltracker.match;

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

public class MatchModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(ResourceBundle.class).annotatedWith(Match.class).toInstance(ResourceBundle.getBundle("match", Locale.ENGLISH));
        bind(SkillCalculator.class).to(FactorGraphTrueSkillCalculator.class).in(Singleton.class);
        bind(MatchMapper.class).toInstance(MatchMapper.INSTANCE);
    }

    @Provides
    @Singleton
    Dao<MatchTable, Integer> gameDao(final ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, MatchTable.class);
    }
}
