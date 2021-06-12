package net.avdw.skilltracker.game;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.gesundkrank.jskills.GameInfo;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.GameRepoAdapter;
import net.avdw.skilltracker.app.service.GameService;
import net.avdw.skilltracker.port.in.GameQuery;
import net.avdw.skilltracker.port.out.GameRepo;

import java.util.Locale;
import java.util.ResourceBundle;

public class GameModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GameMapper.class).toInstance(GameMapper.INSTANCE);
        bind(GameInfo.class).toInstance(GameInfo.getDefaultGameInfo());
        bind(GameQuery.class).to(GameService.class);
        bind(GameRepo.class).to(GameRepoAdapter.class);
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
