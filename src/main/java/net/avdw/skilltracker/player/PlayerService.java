package net.avdw.skilltracker.player;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import org.tinylog.Logger;

import java.util.List;

public class PlayerService {
    private final Dao<PlayerTable, Integer> playerTableDao;

    @Inject
    PlayerService(final Dao<PlayerTable, Integer> playerTableDao) {
        this.playerTableDao = playerTableDao;
    }

    @SneakyThrows
    public PlayerTable createOrRetrievePlayer(final String name) {
        Logger.trace("Create or retrieve player {}", name);
        PlayerTable playerTable = retrievePlayer(name);
        if (playerTable == null) {
            playerTable = playerTableDao.createIfNotExists(new PlayerTable(name));
        }
        return playerTable;
    }

    @SneakyThrows
    public List<PlayerTable> retrieveAllPlayers() {
        return playerTableDao.queryForAll();
    }

    @SneakyThrows
    public PlayerTable retrievePlayer(final String name) {
        return playerTableDao.queryForFirst(playerTableDao.queryBuilder().where().eq(PlayerTable.NAME, name).prepare());
    }

    @SneakyThrows
    public List<PlayerTable> retrievePlayersLikeName(final String name) {
        return playerTableDao.queryBuilder().where().like(PlayerTable.NAME, String.format("%%%s%%", name)).query();
    }

}
