package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.out.GameRepo;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameRepoAdapter implements GameRepo {
    private final Dao<PlayEntity, Integer> playDao;
    private final OrmLiteMapper ormLiteMapper;

    @Inject
    public GameRepoAdapter(Dao<PlayEntity, Integer> playDao, OrmLiteMapper ormLiteMapper) {
        this.playDao = playDao;
        this.ormLiteMapper = ormLiteMapper;
    }

    @SneakyThrows
    @Override
    public Set<Game> findBy(Player player) {
        return playDao.queryBuilder()
                .distinct()
                .selectColumns(PlayEntity.GAME_NAME)
                .where()
                .eq(PlayEntity.PLAYER_NAME, player.getName())
                .query().stream()
                .map(playEntity -> Game.builder()
                        .name(playEntity.getGameName())
                        .build())
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    @Override
    public Optional<Game> findBy(String name) {
        return playDao.queryBuilder()
                .distinct()
                .selectColumns(PlayEntity.GAME_NAME)
                .where()
                .eq(PlayEntity.GAME_NAME, name)
                .query().stream()
                .map(ormLiteMapper::toGame)
                .findAny();
    }

    @SneakyThrows
    @Override
    public Integer totalGames(Player player) {
        return Math.toIntExact(playDao.queryRawValue("SELECT COUNT(DISTINCT GameName)\n" +
                "FROM Play\n" +
                "WHERE PlayerName = ?", player.getName()));
    }

    @SneakyThrows
    @Override
    public Game lastPlayed(Player player) {
        return playDao.queryBuilder()
                .limit(1L)
                .orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(PlayEntity.PLAYER_NAME, player.getName())
                .query().stream()
                .map(ormLiteMapper::toGame)
                .findAny().orElseThrow();
    }
}
