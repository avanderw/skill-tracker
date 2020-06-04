package net.avdw.skilltracker.playground;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import net.avdw.skilltracker.game.GameTable;

import java.sql.SQLException;

public class ORMLitePlayground {
    public static void main(String[] args) throws SQLException {
        String databaseUrl = "jdbc:sqlite:target/test-resources/net.avdw.skilltracker.game/skill-tracker.sqlite";
        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);

        Dao<GameTable, String> gameDao = DaoManager.createDao(connectionSource, GameTable.class);
        gameDao.create(new GameTable("Northgard", 5, 4, 3, 2, 1));

        System.out.println(gameDao.queryForId("Northgard"));
    }
}
