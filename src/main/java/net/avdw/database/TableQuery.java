package net.avdw.database;

import com.google.inject.Inject;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TableQuery<T> {
    private Connection connection;
    private TableBuilder<T> tableBuilder;

    @Inject
    TableQuery(@DbConnection final Connection connection, final TableBuilder<T> tableBuilder) {
        this.connection = connection;
        this.tableBuilder = tableBuilder;
    }

    public List<T> query(final String sql) {
        Logger.trace("Executing query: {}", sql);
        List<T> list = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                list.add(tableBuilder.build(resultSet));
            }
        } catch (SQLException e) {
            Logger.debug(e.getMessage());
        }
        return list;
    }
}
