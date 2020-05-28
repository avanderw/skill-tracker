package net.avdw.database;

import java.sql.ResultSet;

public interface TableBuilder<T> {
    T build(ResultSet resultSet);
}
