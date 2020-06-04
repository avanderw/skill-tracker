package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;

public class GameService {
    private final Dao<GameTable, Integer> gameDao;

    @Inject
    GameService(Dao<GameTable, Integer> gameDao) {
        this.gameDao = gameDao;
    }
    public void createGame(String name, double initialMean, double initialStandardDeviation, double beta, double dynamicsFactor, double drawProbability) {
        GameTable gameTable = new GameTable(name, initialMean, initialStandardDeviation, beta, dynamicsFactor, drawProbability);
        try {
            gameDao.create(gameTable);
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public void deleteGame(String name) {
        try {
            DeleteBuilder<GameTable, Integer> deleteBuilder =
                    gameDao.deleteBuilder();
            deleteBuilder.where().eq("Name", name);
            deleteBuilder.delete();
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public GameTable retrieveGame(String game) {
        try {
            return gameDao.queryForFirst(gameDao.queryBuilder().where().eq("name", game).prepare());
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
