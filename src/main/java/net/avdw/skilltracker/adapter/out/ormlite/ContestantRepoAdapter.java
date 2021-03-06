package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.*;
import net.avdw.skilltracker.port.in.query.SkillQuery;
import net.avdw.skilltracker.port.out.ContestantRepo;

import javax.inject.Inject;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Todo: Logic in this class needs to move into the domain service
 */
public class ContestantRepoAdapter implements ContestantRepo {
    private final Dao<PlayEntity, Integer> playDao;
    private final SkillQuery skillQuery;

    @Inject
    public ContestantRepoAdapter(Dao<PlayEntity, Integer> playDao, SkillQuery skillQuery) {
        this.playDao = playDao;
        this.skillQuery = skillQuery;
    }

    @SneakyThrows
    @Override
    public Contestant mostWinsForGame(Game game) {
        return playDao.queryRaw(String.format("SELECT count(PlayerName), PlayerName FROM Play\n" +
                        "WHERE GameName = '%s'\n" +
                        "AND TeamRank = 1\n" +
                        "GROUP BY PlayerName\n" +
                        "ORDER BY count(PlayerName) DESC, PlayerName ASC\n" +
                        "LIMIT 1", game.getName()), databaseResults -> {
                    Player player = Player.builder().name(databaseResults.getString(1)).build();
                    return Contestant.builder()
                            .player(player)
                            .game(game)
                            .playCount(playCount(game, player))
                            .winCount(databaseResults.getLong(0))
                            .skill(skillQuery.findLatest(game, player))
                            .build();
                }
        ).getResults().stream().findAny().orElseThrow();
    }

    @SneakyThrows
    @Override
    public List<Contestant> topContestantsBySkill(Game game, Long limit) {
        return playDao.queryRaw(String.format("SELECT PlayDate, PlayerName, PlayerMean, PlayerStdDev\n" +
                "FROM (SELECT * FROM Play WHERE GameName = '%s' ORDER BY PlayDate DESC)\n" +
                "GROUP BY PlayerName\n" +
                "ORDER BY PlayerMean - 3 * PlayerStdDev DESC\n" +
                "LIMIT %d", game.getName(), limit), databaseResults ->
                new PlayEntity(null, null, null, null,
                        databaseResults.getTimestamp(0),
                        databaseResults.getString(1),
                        null,
                        databaseResults.getBigDecimal(2),
                        databaseResults.getBigDecimal(3)
                )
        ).getResults().stream()
                .map(t -> {
                    Player player = Player.builder().name(t.getPlayerName()).build();
                    return Contestant.builder()
                            .game(game)
                            .player(player)
                            .skill(Skill.builder()
                                    .date(t.getPlayDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                                    .mean(t.getPlayerMean())
                                    .stdDev(t.getPlayerStdDev())
                                    .build())
                            .winCount(winCount(game, player))
                            .playCount(playCount(game, player))
                            .winStreak(winStreak(game, player))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public Long playCount(Game game, Player player) {
        return playDao.queryRawValue(String.format("SELECT count(GameName) FROM Play\n" +
                "WHERE PlayerName = '%s'\n" +
                "AND GameName = '%s'", player.getName(), game.getName()));
    }

    @Override
    @SneakyThrows
    public Long winCount(Game game, Player player) {
        return playDao.queryRawValue(String.format("SELECT count(PlayerName) FROM Play\n" +
                "WHERE GameName = '%s'\n" +
                "AND PlayerName = '%s'\n" +
                "AND TeamRank = 1", game.getName(), player.getName()));
    }

    @SneakyThrows
    @Override
    public Contestant mostPlayed(Player player) {
        return playDao.queryRaw(String.format("SELECT count(GameName), GameName FROM Play\n" +
                        "WHERE PlayerName = '%s'\n" +
                        "GROUP BY GameName\n" +
                        "ORDER BY count(GameName) DESC\n" +
                        "LIMIT 1", player.getName()), databaseResults -> {
                    Game game = Game.builder().name(databaseResults.getString(1)).build();
                    return Contestant.builder()
                            .player(player)
                            .game(game)
                            .playCount(databaseResults.getLong(0))
                            .winCount(winCount(game, player))
                            .skill(skillQuery.findLatest(game, player))
                            .winStreak(winStreak(game, player))
                            .build();
                }
        ).getResults().stream().findAny().orElseThrow();
    }

    @SneakyThrows
    @Override
    public List<Contestant> contestantsFor(Game game) {
        return playDao.queryBuilder()
                .distinct().selectColumns(PlayEntity.PLAYER_NAME)
                .where().eq(PlayEntity.GAME_NAME, game.getName())
                .query().stream().map(p -> {
                    Player player = Player.builder().name(p.getPlayerName()).build();
                    return Contestant.builder()
                            .game(game)
                            .player(player)
                            .playCount(playCount(game, player))
                            .winCount(winCount(game, player))
                            .skill(skillQuery.findLatest(game, player))
                            .winStreak(winStreak(game, player))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public WinStreak winStreak(Game game, Player player) {
        List<PlayEntity> plays = playDao.queryBuilder()
                .orderBy(PlayEntity.PLAY_DATE, true)
                .where().eq(PlayEntity.GAME_NAME, game.getName())
                .and().eq(PlayEntity.PLAYER_NAME, player.getName())
                .query();

        int curr = 0;
        int longest = 0;
        for (PlayEntity playEntity : plays) {
            if (playEntity.getTeamRank() == 1) {
                curr++;
            } else {
                curr = 0;
            }

            longest = Math.max(curr, longest);
        }

        return WinStreak.builder()
                .current(curr)
                .longest(longest)
                .build();
    }
}
