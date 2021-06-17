package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Ally;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.out.AllyRepo;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

public class AllyRepoAdapter implements AllyRepo {
    private final Dao<PlayEntity, Integer> playDao;

    @Inject
    public AllyRepoAdapter(Dao<PlayEntity, Integer> playDao) {
        this.playDao = playDao;
    }

    @SneakyThrows
    @Override
    public List<Ally> findAll(Game game, Player player) {
        return playDao.queryRaw(String.format("SELECT count(p2.PlayerName), p2.PlayerName FROM Play p1\n" +
                "INNER JOIN Play p2 ON p1.SessionId = p2.SessionId\n" +
                "WHERE p1.PlayerName = '%s'\n" +
                "AND p2.PlayerName != '%s'\n" +
                "AND p1.PlayerTeam = p2.PlayerTeam\n" +
                "AND p1.GameName = '%s'\n" +
                "GROUP BY p2.PlayerName\n" +
                "ORDER BY count(p2.PlayerName) DESC", player.getName(), player.getName(), game.getName()), dbMapper -> {
            Player ally = Player.builder().name(dbMapper.getString(1)).build();
            return Ally.builder()
                    .player(player)
                    .ally(ally)
                    .playCount(dbMapper.getLong(0))
                    .winCount(winCount(player, ally, game))
                    .build();
        }).getResults();
    }

    @SneakyThrows
    @Override
    public List<Ally> findAll(Player player) {
        return playDao.queryRaw(String.format("SELECT count(p2.PlayerName), p2.PlayerName FROM Play p1\n" +
                "INNER JOIN Play p2 ON p1.SessionId = p2.SessionId\n" +
                "WHERE p1.PlayerName = '%s'\n" +
                "AND p2.PlayerName != '%s'\n" +
                "AND p1.PlayerTeam = p2.PlayerTeam\n" +
                "GROUP BY p2.PlayerName\n" +
                "ORDER BY count(p2.PlayerName) DESC", player.getName(), player.getName()), dbMapper -> {
            Player ally = Player.builder().name(dbMapper.getString(1)).build();
            return Ally.builder()
                    .player(player)
                    .ally(ally)
                    .playCount(dbMapper.getLong(0))
                    .winCount(winCount(player, ally))
                    .build();
        }).getResults();
    }

    private Long winCount(Player player, Player ally) {
        try {
            return playDao.queryRawValue(String.format("SELECT count(p2.PlayerName) FROM Play p1\n" +
                    "INNER JOIN Play p2 ON p1.SessionId = p2.SessionId\n" +
                    "WHERE p1.PlayerName = '%s'\n" +
                    "AND p2.PlayerName = '%s'\n" +
                    "AND p1.PlayerTeam = p2.PlayerTeam\n" +
                    "AND p1.TeamRank = 0\n" +
                    "GROUP BY p2.PlayerName\n" +
                    "ORDER BY count(p2.PlayerName) DESC", player.getName(), ally.getName()));
        } catch (SQLException e) {
            return 0L;
        }
    }

    @SneakyThrows
    private Long winCount(Player player, Player ally, Game game) {
        try {
            return playDao.queryRawValue(String.format("SELECT count(p2.PlayerName) FROM Play p1\n" +
                    "INNER JOIN Play p2 ON p1.SessionId = p2.SessionId\n" +
                    "WHERE p1.PlayerName = '%s'\n" +
                    "AND p2.PlayerName = '%s'\n" +
                    "AND p1.PlayerTeam = p2.PlayerTeam\n" +
                    "AND p1.TeamRank = 0\n" +
                    "AND p1.GameName = '%s'\n" +
                    "GROUP BY p2.PlayerName\n" +
                    "ORDER BY count(p2.PlayerName) DESC", player.getName(), ally.getName(), game.getName()));
        } catch (SQLException e) {
            return 0L;
        }
    }
}
