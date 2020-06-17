package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.util.List;

public class GameService {
    private final Dao<GameTable, Integer> gameDao;

    @Inject
    GameService(final Dao<GameTable, Integer> gameDao) {
        this.gameDao = gameDao;
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
}
