package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.out.OpponentRepo;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

public class OpponentRepoAdapter implements OpponentRepo {

    private final Dao<PlayEntity, Integer> playDao;
    private final OrmLiteMapper ormLiteMapper;

    @Inject
    public OpponentRepoAdapter(Dao<PlayEntity, Integer> playDao, OrmLiteMapper ormLiteMapper) {
        this.playDao = playDao;
        this.ormLiteMapper = ormLiteMapper;
    }

    @SneakyThrows
    @Override
    public Set<Player> findBy(Game game, Player player) {
        return playDao.queryRaw("SELECT DISTINCT p2.PlayerName FROM Play p1\n" +
                        "INNER JOIN Play p2 ON p1.sessionId = p2.sessionId\n" +
                        "WHERE p2.playerName != p1.playerName\n" +
                        "AND p2.playerTeam != p1.playerTeam\n" +
                        "AND p1.PlayerName = ?\n" +
                        "AND p1.GameName = ?", playDao.getRawRowMapper(),
                player.getName(), game.getName())
                .getResults().stream()
                .map(ormLiteMapper::toPlayer)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    @Override
    public Integer findOpponentWinCount(Player player, Player opponent, Game game) {
        return Math.toIntExact(playDao.queryRawValue("SELECT count(*) FROM Play p1\n" +
                        "INNER JOIN Play p2 ON p1.sessionId = p2.sessionId\n" +
                        "WHERE p2.playerName != p1.playerName\n" +
                        "AND p2.playerTeam != p1.playerTeam\n" +
                        "AND p2.TeamRank < p1.TeamRank\n" +
                        "AND p1.PlayerName = ?\n" +
                        "AND p2.PlayerName = ?\n" +
                        "AND p1.GameName = ?",
                player.getName(), opponent.getName(), game.getName()));
    }

    @SneakyThrows
    @Override
    public Integer findTotalPlayCount(Player player, Player opponent, Game game) {
        return Math.toIntExact(playDao.queryRawValue("SELECT count(*) FROM Play p1\n" +
                        "INNER JOIN Play p2 ON p1.sessionId = p2.sessionId\n" +
                        "WHERE p2.playerName != p1.playerName\n" +
                        "AND p2.playerTeam != p1.playerTeam\n" +
                        "AND p1.PlayerName = ?\n" +
                        "AND p2.PlayerName = ?\n" +
                        "AND p1.GameName = ?",
                player.getName(), opponent.getName(), game.getName()));
    }
}
