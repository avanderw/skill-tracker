package net.avdw.skilltracker;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;

import javax.inject.Inject;
import java.sql.Connection;

public class DbIntegrity {
    private final Connection connection;

    @Inject
    DbIntegrity(Connection connection) {
        this.connection = connection;
    }

    @SneakyThrows
    public void init() {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase("sqlite/changelog.xml", new ClassLoaderResourceAccessor(), database);
        liquibase.update("");
    }
}
