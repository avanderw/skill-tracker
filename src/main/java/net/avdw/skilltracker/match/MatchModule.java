package net.avdw.skilltracker.match;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.gesundkrank.jskills.SkillCalculator;
import de.gesundkrank.jskills.trueskill.FactorGraphTrueSkillCalculator;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.MatchRepositoryAdapter;
import net.avdw.skilltracker.adapter.out.ormlite.OrmLiteMapper;
import net.avdw.skilltracker.adapter.out.ormlite.OrmLiteMapperImpl;
import net.avdw.skilltracker.port.out.MatchRepository;

import java.util.Locale;
import java.util.ResourceBundle;

public class MatchModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(SkillCalculator.class).to(FactorGraphTrueSkillCalculator.class).in(Singleton.class);
        bind(MatchMapper.class).to(MatchMapperImpl.class);
        bind(MatchRepository.class).to(MatchRepositoryAdapter.class);
        bind(OrmLiteMapper.class).to(OrmLiteMapperImpl.class);
    }


    @Provides
    @Singleton
    @MatchScope
    Templator templatePopulator(@MatchScope final ResourceBundle resourceBundle) {
        return new Templator(resourceBundle);
    }

    @Provides
    @Singleton
    @MatchScope
    ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("match", Locale.ENGLISH);
    }
}
