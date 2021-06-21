package net.avdw.skilltracker.game;

import com.google.inject.AbstractModule;
import de.gesundkrank.jskills.GameInfo;
import net.avdw.skilltracker.adapter.out.ormlite.GameRepoAdapter;
import net.avdw.skilltracker.app.service.GameService;
import net.avdw.skilltracker.port.in.query.GameQuery;
import net.avdw.skilltracker.port.out.GameRepo;

public class GameModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GameMapper.class).toInstance(GameMapper.INSTANCE);
        bind(GameInfo.class).toInstance(GameInfo.getDefaultGameInfo());
        bind(GameQuery.class).to(GameService.class);
        bind(GameRepo.class).to(GameRepoAdapter.class);
    }
}
