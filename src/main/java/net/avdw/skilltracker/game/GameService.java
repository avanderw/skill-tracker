package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import lombok.SneakyThrows;
import net.avdw.skilltracker.match.MatchTable;
import net.avdw.skilltracker.player.PlayerTable;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameService {
    private final Dao<GameTable, Integer> gameDao;
    private final Dao<MatchTable, Integer> matchDao;

    @Inject
    GameService(final Dao<GameTable, Integer> gameDao, final Dao<MatchTable, Integer> matchDao) {
        this.gameDao = gameDao;
        this.matchDao = matchDao;
    }

    @SneakyThrows
    public void createGame(final String game, final BigDecimal drawProbability) {
        gameDao.create(new GameTable(drawProbability, game));
    }

    @SneakyThrows
    public void deleteGame(final String name) {
        final DeleteBuilder<GameTable, Integer> deleteBuilder =
                gameDao.deleteBuilder();
        deleteBuilder.where().eq("Name", name);
        deleteBuilder.delete();
    }

    @SneakyThrows
    public List<GameTable> retrieveAllGames() {
        return gameDao.queryForAll();
    }

    @SneakyThrows
    public GameTable retrieveGame(final String name) {
        return gameDao.queryForFirst(gameDao.queryBuilder().where().like(GameTable.NAME, String.format("%%%s%%", name)).prepare());
    }

    @SneakyThrows
    public List<GameTable> retrieveGamesLikeName(final String name) {
        return gameDao.queryBuilder().where().like(GameTable.NAME, String.format("%%%s%%", name)).query();
    }

    @SneakyThrows
    public List<MatchTable> retrieveTopGamesForPlayer(final PlayerTable playerTable, final Long limit) {
        List<MatchTable> allMatchList = matchDao.queryBuilder().orderBy(MatchTable.PLAY_DATE, false)
                .where().eq(MatchTable.PLAYER_FK, playerTable).query();

        Map<String, MatchTable> gameMatchTableMap = new HashMap<>();
        allMatchList.forEach(matchTable -> gameMatchTableMap.putIfAbsent(matchTable.getGameTable().getName(), matchTable));

        return gameMatchTableMap.values().stream()
                .limit(limit)
                .sorted(Comparator.comparing((MatchTable matchTable) -> matchTable.getMean()
                        .subtract(matchTable.getStandardDeviation().multiply(new BigDecimal(3))))
                        .reversed())
                .collect(Collectors.toList());
    }
}
