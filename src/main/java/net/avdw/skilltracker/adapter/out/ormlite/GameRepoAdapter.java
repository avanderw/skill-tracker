package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.out.GameRepo;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameRepoAdapter implements GameRepo {
    private final Dao<PlayEntity, Integer> playDao;
    private final DbMapper dbMapper;

    @Inject
    public GameRepoAdapter(Dao<PlayEntity, Integer> playDao, DbMapper dbMapper) {
        this.playDao = playDao;
        this.dbMapper = dbMapper;
    }

    @SneakyThrows
    @Override
    public Set<Game> findGamesFor(Player player) {
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
    public Optional<Game> findGamesFor(String name) {
        return playDao.queryBuilder()
                .distinct()
                .selectColumns(PlayEntity.GAME_NAME)
                .where()
                .eq(PlayEntity.GAME_NAME, name)
                .query().stream()
                .map(dbMapper::toGame)
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
                .map(dbMapper::toGame)
                .findAny().orElseThrow();
    }

    @SneakyThrows
    @Override
    public List<Game> findAll() {
        return playDao.queryBuilder()
                .distinct()
                .selectColumns(PlayEntity.GAME_NAME)
                .query().stream()
                .map(dbMapper::toGame)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public List<Game> findLike(String search) {
        return playDao.queryBuilder()
                .distinct()
                .selectColumns(PlayEntity.GAME_NAME)
                .where().like(PlayEntity.GAME_NAME, String.format("%%%s%%", search))
                .query().stream()
                .map(dbMapper::toGame)
                .collect(Collectors.toList());
    }
}
