package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteMatch;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameService {
    private final Dao<PlayEntity, Integer> playDao;

    @Inject
    GameService(Dao<PlayEntity, Integer> playDao) {
        this.playDao = playDao;
    }


    @SneakyThrows
    public void deleteGame(final String name) {
        final PlayEntity ormLiteGame = playDao.queryBuilder().where().like(PlayEntity.GAME_NAME, name).queryForFirst();
        if (ormLiteGame == null) {
            Logger.debug("Cannot find game to delete with name {}", name);
            return;
        }
        final DeleteBuilder<PlayEntity, Integer> matchDeleteBuilder = playDao.deleteBuilder();
        matchDeleteBuilder.where().eq(PlayEntity.GAME_NAME, name);
        matchDeleteBuilder.delete();
        playDao.delete(ormLiteGame);
    }

    @SneakyThrows
    public List<OrmLiteGame> retrieveAllGames() {
        return playDao.queryBuilder().distinct()
                .selectColumns(PlayEntity.GAME_NAME)
                .query().stream()
                .map(p -> new OrmLiteGame(p.getGameName()))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public OrmLiteGame retrieveGame(final String name) {
        return playDao.queryBuilder().where().eq(PlayEntity.GAME_NAME, name).queryForFirst() != null ? new OrmLiteGame(name) : null;
    }

    @SneakyThrows
    public List<OrmLiteGame> retrieveGamesLikeName(final String name) {
        return playDao.queryBuilder().distinct()
                .selectColumns(PlayEntity.GAME_NAME)
                .where().like(PlayEntity.GAME_NAME, String.format("%%%s%%", name)).query().stream()
                .map(p -> new OrmLiteGame(p.getGameName()))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public List<PlayEntity> retrieveTopGamesForPlayer(final OrmLitePlayer ormLitePlayer, final Long limit) {
        List<PlayEntity> allMatchList = playDao.queryBuilder().orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(OrmLiteMatch.PLAYER_FK, ormLitePlayer).query();

        Map<String, PlayEntity> gameMatchTableMap = new HashMap<>();
        allMatchList.forEach(matchTable -> gameMatchTableMap.putIfAbsent(matchTable.getGameName(), matchTable));

        return gameMatchTableMap.values().stream()
                .limit(limit)
                .sorted(Comparator.comparing((PlayEntity ormLiteMatch) -> ormLiteMatch.getPlayerMean()
                        .subtract(ormLiteMatch.getPlayerStdDev().multiply(new BigDecimal(3))))
                        .reversed())
                .collect(Collectors.toList());
    }
}
