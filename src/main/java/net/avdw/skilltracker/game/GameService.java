package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class GameService {
    private final Dao<GameTable, Integer> gameDao;

    @Inject
    GameService(final Dao<GameTable, Integer> gameDao) {
        this.gameDao = gameDao;
    }

    public void createGame(final String game, final BigDecimal drawProbability) {
        try {
            gameDao.create(new GameTable(drawProbability, game));
        } catch (final SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public void deleteGame(final String name) {
        try {
            final DeleteBuilder<GameTable, Integer> deleteBuilder =
                    gameDao.deleteBuilder();
            deleteBuilder.where().eq("Name", name);
            deleteBuilder.delete();
        } catch (final SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public List<GameTable> retrieveAllGames() {
        try {
            return gameDao.queryForAll();
        } catch (final SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public GameTable retrieveGame(final String name) {
        try {
            return gameDao.queryForFirst(gameDao.queryBuilder().where().like(GameTable.NAME, String.format("%%%s%%", name)).prepare());
        } catch (final SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public List<GameTable> retrieveGamesLikeName(final String name) {
        try {
            return gameDao.queryBuilder().where().like(GameTable.NAME, String.format("%%%s%%", name)).query();
        } catch (final SQLException e) {
            throw new UnsupportedOperationException();
        }

    }
}
