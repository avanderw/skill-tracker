package net.avdw.skilltracker;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import net.avdw.database.DbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseModule extends AbstractModule {

    @Provides
    @Singleton
    @DbConnection
    Connection databaseConnection(@Named(PropertyName.JDBC_URL) final String jdbcUrl) throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    @Provides
    @Singleton
    ConnectionSource connectionSource(@Named(PropertyName.JDBC_URL) final String jdbcUrl) throws SQLException {
        return new JdbcConnectionSource(jdbcUrl);
    }
}
