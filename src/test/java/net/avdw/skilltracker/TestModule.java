package net.avdw.skilltracker;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import net.avdw.skilltracker.adapter.out.ormlite.MatchRepoAdapter;
import net.avdw.skilltracker.adapter.out.ormlite.OpponentRepoAdapter;
import net.avdw.skilltracker.adapter.out.ormlite.RankRepoAdapter;
import net.avdw.skilltracker.adapter.out.ormlite.SkillRepoAdapter;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.app.service.*;
import net.avdw.skilltracker.game.GameModule;
import net.avdw.skilltracker.match.MatchModule;
import net.avdw.skilltracker.player.PlayerModule;
import net.avdw.skilltracker.port.in.*;
import net.avdw.skilltracker.port.out.MatchRepo;
import net.avdw.skilltracker.port.out.OpponentRepo;
import net.avdw.skilltracker.port.out.RankRepo;
import net.avdw.skilltracker.port.out.SkillRepo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class TestModule extends AbstractModule {
    private final String jdbcUrl;

    public TestModule(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    @Override
    protected void configure() {
        Names.bindProperties(binder(), defaultProperties());
        bind(List.class).to(LinkedList.class);

        install(new GameModule());
        install(new PlayerModule());
        install(new MatchModule());

        bind(StatsQuery.class).to(StatsService.class);
        bind(NemesisQuery.class).to(NemesisService.class);
        bind(MinionQuery.class).to(MinionService.class);
        bind(MatchupQuery.class).to(MatchupService.class);
        bind(OpponentQuery.class).to(OpponentService.class);
        bind(OpponentRepo.class).to(OpponentRepoAdapter.class);
        bind(SkillQuery.class).to(SkillService.class);
        bind(SkillRepo.class).to(SkillRepoAdapter.class);
        bind(RankQuery.class).to(RankService.class);
        bind(RankRepo.class).to(RankRepoAdapter.class);
        bind(MatchQuery.class).to(MatchService.class);
        bind(MatchRepo.class).to(MatchRepoAdapter.class);
    }

    @Provides
    @Singleton
    Connection databaseConnection(@Named(PropertyName.JDBC_URL) final String jdbcUrl) throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    @Provides
    @Singleton
    ConnectionSource connectionSource(@Named(PropertyName.JDBC_URL) final String jdbcUrl) throws SQLException {
        return new JdbcConnectionSource(jdbcUrl);
    }

    protected Properties defaultProperties() {
        Properties properties = new Properties();
        properties.put(PropertyName.JDBC_URL, String.format("jdbc:sqlite:%s", jdbcUrl));
        return properties;
    }

    @Provides
    @Singleton
    Dao<PlayEntity, Integer> playDao(final ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, PlayEntity.class);
    }

    @Provides
    @Singleton
    @SkillTracker
    Templator templator(@SkillTracker final ResourceBundle resourceBundle) {
        return new Templator(resourceBundle);
    }

    @Provides
    @Singleton
    @SkillTracker
    ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("skilltracker", Locale.ENGLISH);
    }
}
