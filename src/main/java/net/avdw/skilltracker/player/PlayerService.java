package net.avdw.skilltracker.player;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.match.MatchTable;
import org.tinylog.Logger;

import java.util.List;

public class PlayerService {
    private final Dao<PlayerTable, Integer> playerTableDao;
    private final Dao<MatchTable, Integer> matchTableDao;

    @Inject
    PlayerService(final Dao<PlayerTable, Integer> playerTableDao, final Dao<MatchTable, Integer> matchTableDao) {
        this.playerTableDao = playerTableDao;
        this.matchTableDao = matchTableDao;
    }

    @SneakyThrows
    public void changeName(final PlayerTable fromPlayer, final String toName) {
        fromPlayer.setName(toName);
        playerTableDao.update(fromPlayer);
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
    public void removePlayer(final PlayerTable fromPlayer) {
        playerTableDao.delete(fromPlayer);
    }

    @SneakyThrows
    public void removePlayersWithNoMatches() {
        for (final PlayerTable p : playerTableDao.queryForAll()) {
            if (matchTableDao.queryForEq(MatchTable.PLAYER_FK, p).isEmpty()) {
                playerTableDao.delete(p);
            }
        }
    }

    @SneakyThrows
    public List<PlayerTable> retrieveAllPlayers() {
        return playerTableDao.queryForAll();
    }

    @SneakyThrows
    public PlayerTable retrievePlayer(final String name) {
        return playerTableDao.queryForFirst(playerTableDao.queryBuilder().where().like(PlayerTable.NAME, name).prepare());
    }

    @SneakyThrows
    public List<PlayerTable> retrievePlayersLikeName(final String name) {
        return playerTableDao.queryBuilder().where().like(PlayerTable.NAME, String.format("%%%s%%", name)).query();
    }

}
