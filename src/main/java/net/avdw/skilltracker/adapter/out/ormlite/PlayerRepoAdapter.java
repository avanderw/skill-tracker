package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.out.PlayerRepo;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerRepoAdapter implements PlayerRepo {

    private final Dao<PlayEntity, Integer> playDao;
    private final DbMapper dbMapper;

    @Inject
    public PlayerRepoAdapter(Dao<PlayEntity, Integer> playDao, DbMapper dbMapper) {
        this.playDao = playDao;
        this.dbMapper = dbMapper;
    }

    @SneakyThrows
    @Override
    public Optional<Player> findBy(String name) {
        return playDao.queryBuilder()
                .distinct()
                .selectColumns(PlayEntity.PLAYER_NAME)
                .where()
                .eq(PlayEntity.PLAYER_NAME, name)
                .query().stream()
                .map(dbMapper::toPlayer)
                .findAny();
    }

    @SneakyThrows
    @Override
    public Set<Player> findBy(Game game) {
        return playDao.queryBuilder()
                .distinct()
                .selectColumns(PlayEntity.PLAYER_NAME)
                .where().eq(PlayEntity.GAME_NAME, game.getName())
                .query().stream()
                .map(dbMapper::toPlayer)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    @Override
    public Set<Player> findAll() {
        return playDao.queryBuilder()
                .distinct()
                .selectColumns(PlayEntity.PLAYER_NAME)
                .query().stream()
                .map(dbMapper::toPlayer)
                .collect(Collectors.toSet());
    }

}
