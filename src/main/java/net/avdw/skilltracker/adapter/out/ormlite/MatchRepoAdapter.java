package net.avdw.skilltracker.adapter.out.ormlite;

import com.j256.ormlite.dao.Dao;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.*;
import net.avdw.skilltracker.port.out.ContestantRepo;
import net.avdw.skilltracker.port.out.MatchRepo;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class MatchRepoAdapter implements MatchRepo {

    private final Dao<PlayEntity, Integer> playDao;
    private final ContestantRepo contestantRepo;

    @Inject
    public MatchRepoAdapter(Dao<PlayEntity, Integer> playDao, ContestantRepo contestantRepo) {
        this.playDao = playDao;
        this.contestantRepo = contestantRepo;
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
                .map(p -> p.getPlayDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .findAny().orElseThrow();
    }

    @SneakyThrows
    @Override
    public List<Match> findLastBy(Game game, Long limit) {
        return playDao.queryBuilder()
                .groupBy(PlayEntity.SESSION_ID)
                .limit(limit)
                .orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(PlayEntity.GAME_NAME, game.getName())
                .query().stream()
                .map(p -> Match.builder()
                        .date(p.getPlayDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                        .sessionId(p.getSessionId().substring(0, p.getSessionId().indexOf("-")))
                        .teams(teamQuery(p.getSessionId()))
                        .build())
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private List<Team> teamQuery(String sessionId) {
        return playDao.queryBuilder()
                .where().eq(PlayEntity.SESSION_ID, sessionId)
                .query().stream()
                .collect(Collectors.groupingBy(PlayEntity::getPlayerTeam)).entrySet().stream()
                .map(e -> Team.builder()
                        .rank(e.getKey())
                        .contestants(e.getValue().stream().map(p -> {
                            Game game = Game.builder()
                                    .name(p.getGameName())
                                    .build();
                            Player player = Player.builder()
                                    .name(p.getPlayerName())
                                    .build();
                            return Contestant.builder()
                                    .game(game)
                                    .player(player)
                                    .skill(Skill.builder()
                                            .date(p.getPlayDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                                            .mean(p.getPlayerMean())
                                            .stdDev(p.getPlayerStdDev())
                                            .build())
                                    .winCount(contestantRepo.winCount(game, player))
                                    .playCount(contestantRepo.playCount(game, player))
                                    .build();
                        }).collect(Collectors.toList()))
                        .build()
                )
                .collect(Collectors.toList());
    }
}
