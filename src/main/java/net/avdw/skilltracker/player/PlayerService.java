package net.avdw.skilltracker.player;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class PlayerService {
    private final Dao<PlayerTable, Integer> playerTableDao;

    @Inject
    PlayerService(final Dao<PlayerTable, Integer> playerTableDao) {
        this.playerTableDao = playerTableDao;
    }

    public PlayerTable buildPlayer(final String name) {
        try {
            PlayerTable playerTable = playerTableDao.queryForFirst(playerTableDao.queryBuilder().where().eq(PlayerTable.NAME, name).prepare());
            if (playerTable == null) {
                throw new PlayerNotFoundException();
            }
            return playerTable;
        } catch (final SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public PlayerTable createOrRetrievePlayer(final String name) {
        try {
            PlayerTable playerTable = new PlayerTable(name);
            playerTableDao.create(playerTable);
            return playerTable;
        } catch (final SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public List<PlayerTable> retrieveAllPlayers() {
        try {
            return playerTableDao.queryForAll();
        } catch (final SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public PlayerTable retrievePlayer(final String name) {
        try {
            return playerTableDao.queryForFirst(playerTableDao.queryBuilder().where().eq(PlayerTable.NAME, name).prepare());
        } catch (final SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public List<PlayerTable> retrievePlayersLikeName(final String name) {
        try {
            return playerTableDao.queryBuilder().where().like(PlayerTable.NAME, String.format("%%%s%%", name)).query();
        } catch (final SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
