package net.avdw.skilltracker.player;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.PlayerRepoAdapter;
import net.avdw.skilltracker.app.service.PlayerService;
import net.avdw.skilltracker.port.in.PlayerQuery;
import net.avdw.skilltracker.port.out.PlayerRepo;

import java.util.Locale;
import java.util.ResourceBundle;

public class PlayerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PlayerQuery.class).to(PlayerService.class);
        bind(PlayerRepo.class).to(PlayerRepoAdapter.class);
    }

    @Provides
    @Singleton
    ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("player", Locale.ENGLISH);
    }

    @Provides
    @Singleton
    Templator templatePopulator(final ResourceBundle resourceBundle) {
        return new Templator(resourceBundle);
    }
}
