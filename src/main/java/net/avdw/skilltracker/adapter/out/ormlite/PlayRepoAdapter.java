package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Play;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.out.PlayRepo;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class PlayRepoAdapter implements PlayRepo {
    private final Dao<PlayEntity, Integer> playDao;
    private final DbMapper dbMapper;

    @Inject
    public PlayRepoAdapter(Dao<PlayEntity, Integer> playDao, DbMapper dbMapper) {
        this.playDao = playDao;
        this.dbMapper = dbMapper;
    }

    @SneakyThrows
    @Override
    public List<Game> findAllGamesFor(Player player) {
        return playDao.queryBuilder()
                .distinct()
                .selectColumns(PlayEntity.GAME_NAME)
                .where()
                .eq(PlayEntity.PLAYER_NAME, player.getName())
                .query().stream()
                .map(dbMapper::toGame)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public Long lookupPlayCountFor(Game game, Player player) {
        return playDao.queryBuilder()
                .where().eq(PlayEntity.GAME_NAME, game.getName())
                .and().eq(PlayEntity.PLAYER_NAME, player.getName())
                .countOf();
    }

    @SneakyThrows
    @Override
    public Play lookupFirstPlay(Player player) {
        return dbMapper.toPlay(playDao.queryBuilder()
                .orderBy(PlayEntity.PLAY_DATE, true)
                .where().eq(PlayEntity.PLAYER_NAME, player.getName())
                .queryForFirst());
    }

}
