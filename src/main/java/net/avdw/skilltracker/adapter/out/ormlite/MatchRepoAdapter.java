package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.out.MatchRepo;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;

public class MatchRepoAdapter implements MatchRepo {

    private final Dao<PlayEntity, Integer> playDao;

    @Inject
    public MatchRepoAdapter(Dao<PlayEntity, Integer> playDao) {
        this.playDao = playDao;
    }

    @SneakyThrows
    @Override
    public Integer totalMatches(Game game, Player player) {
        return Math.toIntExact(playDao.queryRawValue("SELECT count(DISTINCT SessionID)\n" +
                "FROM Play\n" +
                "WHERE GameName = ?\n" +
                "AND PlayerName = ?", game.getName(), player.getName()));
    }

    @SneakyThrows
    @Override
    public Integer totalMatches(Player player) {
        return Math.toIntExact(playDao.queryRawValue("SELECT count(*)\n" +
                "FROM Play\n" +
                "WHERE PlayerName = ?", player.getName()));
    }

    @SneakyThrows
    @Override
    public LocalDate lastPlayedDate(Player player) {
        return playDao.queryBuilder()
                .limit(1L)
                .orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(PlayEntity.PLAYER_NAME, player.getName())
                .query().stream()
                .map(p->p.getPlayDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .findAny().orElseThrow();
    }
}
