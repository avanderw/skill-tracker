package net.avdw.skilltracker.player;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class PlayerService {
    private final Dao<PlayerTable, Integer> playerTableDao;

    @Inject
    PlayerService(final Dao<PlayerTable, Integer> playerTableDao) {
        this.playerTableDao = playerTableDao;
    }
    public PlayerTable createOrRetrievePlayer(final String name) {
        try {
            PlayerTable playerTable = new PlayerTable(name);
            playerTableDao.create(playerTable);
            return playerTable;
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public PlayerTable retrievePlayer(String name) {
        try {
            return playerTableDao.queryForFirst(playerTableDao.queryBuilder().where().eq(PlayerTable.NAME, name).prepare());
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
