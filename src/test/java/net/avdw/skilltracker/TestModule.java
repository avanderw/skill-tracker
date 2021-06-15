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
import net.avdw.skilltracker.adapter.out.ormlite.*;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.app.ContestantService;
import net.avdw.skilltracker.app.service.*;
import net.avdw.skilltracker.game.GameModule;
import net.avdw.skilltracker.match.MatchModule;
import net.avdw.skilltracker.player.PlayerModule;
import net.avdw.skilltracker.port.in.query.*;
import net.avdw.skilltracker.port.in.query.stat.CamaraderieQuery;
import net.avdw.skilltracker.port.in.query.stat.ComradeQuery;
import net.avdw.skilltracker.port.in.query.stat.MinionQuery;
import net.avdw.skilltracker.port.in.query.stat.NemesisQuery;
import net.avdw.skilltracker.port.out.*;

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
        install(new HexagonalModule());
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
