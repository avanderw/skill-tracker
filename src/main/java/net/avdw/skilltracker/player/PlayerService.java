package net.avdw.skilltracker.player;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class PlayerService {
    private final Dao<PlayerTable, Integer> playerTableDao;

    @Inject
    PlayerService(Dao<PlayerTable, Integer> playerTableDao) {
        this.playerTableDao = playerTableDao;
    }
    public PlayerTable createOrRetrievePlayer(String name) {
        try {
            PlayerTable playerTable = new PlayerTable(name);
            playerTableDao.create(playerTable);
            return playerTable;
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
