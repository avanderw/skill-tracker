package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Skill;
import net.avdw.skilltracker.port.out.SkillRepo;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.List;

public class SkillRepoAdapter implements SkillRepo {

    private final Dao<PlayEntity, Integer> playDao;

    @Inject
    public SkillRepoAdapter(Dao<PlayEntity, Integer> playDao) {
        this.playDao = playDao;
    }

    @SneakyThrows
    @Override
    public Skill findBy(Game game, Player player) {
        PlayEntity playEntity = playDao.queryBuilder()
                .orderBy(PlayEntity.PLAY_DATE, false)
                .limit(1L)
                .where().eq(PlayEntity.GAME_NAME, game.getName())
                .and().eq(PlayEntity.PLAYER_NAME, player.getName())
                .queryForFirst();

        List<PlayEntity> duplicatePlayers = playDao.queryBuilder()
                .where().eq(PlayEntity.GAME_NAME, game.getName())
                .and().eq(PlayEntity.PLAYER_NAME, player.getName())
                .and().eq(PlayEntity.SESSION_ID, playEntity.getSessionId())
                .query();


        BigDecimal mean = duplicatePlayers.stream()
                .map(PlayEntity::getPlayerMean)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(duplicatePlayers.size()), RoundingMode.HALF_UP);
        BigDecimal stddev = duplicatePlayers.stream()
                .map(PlayEntity::getPlayerStdDev)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(duplicatePlayers.size()), RoundingMode.HALF_UP);


        return Skill.builder()
                .date(playEntity.getPlayDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .mean(mean)
                .stdDev(stddev)
                .build();
    }
}
