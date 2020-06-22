package net.avdw.skilltracker.player;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.match.MatchTable;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerService {
    private final Dao<MatchTable, Integer> matchTableDao;
    private final Dao<PlayerTable, Integer> playerTableDao;

    @Inject
    PlayerService(final Dao<PlayerTable, Integer> playerTableDao, final Dao<MatchTable, Integer> matchTableDao) {
        this.playerTableDao = playerTableDao;
        this.matchTableDao = matchTableDao;
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

    public PlayerTable instanceOrRetrievePlayer(final String name) {
        Logger.trace("Instance or retrieve player {}", name);
        PlayerTable playerTable = retrievePlayer(name);
        if (playerTable == null) {
            playerTable = new PlayerTable(name);
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

    @SneakyThrows
    public List<MatchTable> retrieveTopPlayersForGame(final GameTable gameTable, final Long limit) {
        List<MatchTable> matchTableList = matchTableDao.queryBuilder()
                .orderBy(MatchTable.PLAY_DATE, false)
                .where().eq(MatchTable.GAME_FK, gameTable).query();

        Map<String, MatchTable> playerMatchMap = new HashMap<>();
        matchTableList.forEach(matchTable -> playerMatchMap.putIfAbsent(matchTable.getPlayerTable().getName(), matchTable));

        return playerMatchMap.values().stream()
                .sorted(Comparator.comparing((MatchTable m)->m.getMean().subtract(m.getStandardDeviation().multiply(BigDecimal.valueOf(3)))).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
