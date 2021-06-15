package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.PriorityObject;
import net.avdw.skilltracker.domain.Skill;
import net.avdw.skilltracker.port.out.RankRepo;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RankRepoAdapter implements RankRepo {

    private final Dao<PlayEntity, Integer> playDao;

    @Inject
    public RankRepoAdapter(Dao<PlayEntity, Integer> playDao) {
        this.playDao = playDao;
    }

    @SneakyThrows
    @Override
    public Integer findBy(Game game, Player player) {
        return playDao.queryRaw("SELECT MAX(PlayDate), PlayerName, PlayerMean, PlayerStdDev\n" +
                        "FROM Play\n" +
                        "WHERE GameName = ?\n" +
                        "GROUP BY PlayerName", dbResult -> PriorityObject.<Player>builder()
                        .object(Player.builder()
                                .name(dbResult.getString(1))
                                .build())
                        .priority(Skill.builder()
                                .date(dbResult.getTimestamp(0).toLocalDateTime().toLocalDate())
                                .mean(dbResult.getBigDecimal(2))
                                .stdDev(dbResult.getBigDecimal(3))
                                .build().getLow())
                        .build(),
                game.getName()).getResults().stream()
                .sorted(Comparator.comparing((PriorityObject<Player> p) -> p.getPriority().doubleValue()).reversed())
                .map(PriorityObject::getObject)
                .collect(Collectors.toList())
                .indexOf(player) + 1;
    }
}
