package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import de.gesundkrank.jskills.*;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteMatch;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.calculator.CalculatorService;
import net.avdw.skilltracker.game.GameMapper;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MatchService {
    private final CalculatorService calculatorService;
    private final GameMapper gameMapper;
    private final MatchDataBuilder matchDataBuilder;
    private final MatchMapper matchMapper;
    private final Dao<PlayEntity, Integer> playDao;
    private final SkillCalculator skillCalculator;

    @Inject
    MatchService(
                 final SkillCalculator skillCalculator,
                 final MatchMapper matchMapper,
                 final GameMapper gameMapper,
                 final CalculatorService calculatorService,
                 final MatchDataBuilder matchDataBuilder, Dao<PlayEntity, Integer> playDao) {
        this.skillCalculator = skillCalculator;
        this.matchMapper = matchMapper;
        this.gameMapper = gameMapper;
        this.calculatorService = calculatorService;
        this.matchDataBuilder = matchDataBuilder;
        this.playDao = playDao;
    }

    public BigDecimal calculateMatchQuality(final OrmLiteGame ormLiteGame, final List<ITeam> teams) {
        GameInfo gameInfo = gameMapper.toGameInfo(ormLiteGame);
        return BigDecimal.valueOf(skillCalculator.calculateMatchQuality(gameInfo, teams));
    }

    private PlayEntity collapseMatchTable(final OrmLiteGame ormLiteGame, final OrmLitePlayer ormLitePlayer, final List<PlayEntity> ormLiteMatchList) {
        PlayEntity ormLiteMatch;
        if (ormLiteMatchList.isEmpty()) {
            Logger.debug("{} does not appear in an earlier match", ormLitePlayer.getName());
            ormLiteMatch = matchMapper.toMatchTable(ormLiteGame, ormLitePlayer, gameMapper.toRating(ormLiteGame));
        } else if (ormLiteMatchList.size() == 1) {
            ormLiteMatch = ormLiteMatchList.get(0);
        } else {
            Date lastPlayed = ormLiteMatchList.get(0).getPlayDate();
            Map<Date, List<PlayEntity>> dateMatchTableList = ormLiteMatchList.stream().collect(Collectors.groupingBy(PlayEntity::getPlayDate));
            if (dateMatchTableList.get(lastPlayed).size() == 1) {
                Logger.debug("{} appears once in match", ormLitePlayer.getName());
                ormLiteMatch =dateMatchTableList.get(lastPlayed).get(0);
            } else {
                Logger.debug("{} appears twice in match, returning average of means & stdevs", ormLitePlayer.getName());
                ormLiteMatch = dateMatchTableList.get(lastPlayed).get(0);
                ormLiteMatch.setPlayerMean(BigDecimal.valueOf(dateMatchTableList.get(lastPlayed).stream().mapToDouble(m -> m.getPlayerMean().doubleValue()).average().orElseThrow(UnsupportedOperationException::new)));
                ormLiteMatch.setPlayerStdDev(BigDecimal.valueOf(dateMatchTableList.get(lastPlayed).stream().mapToDouble(m -> m.getPlayerStdDev().doubleValue()).average().orElseThrow(UnsupportedOperationException::new)));
            }
        }
        return ormLiteMatch;
    }

    @SneakyThrows
    public void combinePlayer(final OrmLitePlayer fromPlayer, final OrmLitePlayer toPlayer) {
        UpdateBuilder<PlayEntity, Integer> update = playDao.updateBuilder()
                .updateColumnValue(PlayEntity.PLAYER_NAME, toPlayer.getName());
        update.where().eq(PlayEntity.PLAYER_NAME, fromPlayer.getName());
        update.update();

        recalculate(toPlayer.getName());
    }

    @SneakyThrows
    public List<PlayEntity> createMatchForGame(final OrmLiteGame ormLiteGame, final List<ITeam> teams, final int... ranks) {
        Logger.debug("create match for game={}, ranks={}, teams={}", ormLiteGame.getName(), Arrays.toString(ranks), teams);
        String sessionId = UUID.randomUUID().toString();
        Date date = new Date();
        GameInfo gameInfo = gameMapper.toGameInfo(ormLiteGame);
        Map<IPlayer, Rating> newRatings = skillCalculator.calculateNewRatings(gameInfo, teams, Arrays.copyOf(ranks, ranks.length));
        List<PlayEntity> ormLiteMatchList = new ArrayList<>();
        newRatings.forEach((p, r) -> {
            OrmLitePlayer ormLitePlayer = (OrmLitePlayer) p;
            Logger.debug("Processing skill for {}", p);

            PlayEntity ormLiteMatch = matchMapper.toMatchTable(ormLiteGame, ormLitePlayer, r);
            ormLiteMatch.setSessionId(sessionId);
            ormLiteMatch.setPlayDate(date);
            int teamIdx = -1;
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).keySet().stream().anyMatch(key -> key.equals(ormLitePlayer))) {
                    Logger.debug("\t> OLD {}={}", ormLitePlayer.getName(), teams.get(i).get(ormLitePlayer));
                    Logger.debug("\t> NEW {}={}", ormLitePlayer.getName(), r);

                    teamIdx = i;
                    break;
                }
            }

            ormLiteMatch.setPlayerTeam(teamIdx);
            ormLiteMatch.setTeamRank(ranks[teamIdx] -1);
            Logger.debug("Saving {}", ormLiteMatch);
            ormLiteMatchList.add(ormLiteMatch);
        });
        playDao.create(ormLiteMatchList);
        return ormLiteMatchList;
    }

    @SneakyThrows
    public boolean deleteMatch(final String partial) {
        List<PlayEntity> matchList = playDao.queryBuilder().where().like(PlayEntity.SESSION_ID, String.format("%s%%", partial)).query();
        List<String> playerList = matchList.stream().map(PlayEntity::getPlayerName).collect(Collectors.toList());
        boolean deleted = playDao.delete(matchList) > 0;
        if (deleted) {
            playerList.forEach(this::recalculate);
        }
        return deleted;
    }

    @SneakyThrows
    public List<OrmLiteGame> gameListForPlayer(final OrmLitePlayer ormLitePlayer) {
        return playDao.queryBuilder().groupBy(PlayEntity.GAME_NAME)
                .where().eq(OrmLiteMatch.PLAYER_FK, ormLitePlayer).query().stream()
                .map(p->new OrmLiteGame(p.getGameName()))
                .collect(Collectors.toList());
    }

    public void recalculate(final String player) {
        Logger.debug("=> recalculate {}", player);
        List<PlayEntity> matchList = retrieveAllMatchesForPlayer(new OrmLitePlayer(player));
        matchList.stream().map(m -> m.getPlayDate().getTime()).forEach(l -> Logger.debug("session={}", l));
        matchList.stream().sorted(Comparator.comparing(PlayEntity::getPlayDate)).forEach(match -> {
            Logger.debug("recalculate => player={}, session={}", player, match.getSessionId());
            List<PlayEntity> sessionList = retrieveMatchWithSessionId(match.getSessionId());
            Date playDate = sessionList.stream().findAny().orElseThrow().getPlayDate();
            Logger.debug("recalculate => playDate={}", playDate.getTime());
            OrmLiteGame ormLiteGame = new OrmLiteGame(sessionList.stream().findAny().orElseThrow().getGameName());

            Logger.debug("recalculate => {}", player);
            Rating lastRating = retrieveRatingBefore(playDate, new OrmLitePlayer(player), ormLiteGame);
            Logger.debug("lastRating => {}", lastRating);
            Map<String, Map<String, Rating>> lastPlayerListGameListRatingMap = new HashMap<>();
            MatchData matchData = matchDataBuilder.buildFromMatchTable(sessionList);
            matchData.getTeamDataSet().forEach(team -> team.getOrmLitePlayerSet().forEach(p -> {
                Logger.debug("recalculate => team player {}", p.getName());
                lastPlayerListGameListRatingMap.putIfAbsent(p.getName(), new HashMap<>());
                lastPlayerListGameListRatingMap.get(p.getName()).putIfAbsent(ormLiteGame.getName(), retrieveRatingBefore(playDate, p, ormLiteGame));
            }));

            lastPlayerListGameListRatingMap.forEach((k, v) -> Logger.debug("lastPlayerListGameListRatingMap => {}={}", k, v));
            Map<IPlayer, Rating> playerNewRatingMap = calculatorService.calculate(sessionList, lastPlayerListGameListRatingMap);
            Logger.debug("New ratings for {}", ormLiteGame.getName());
            playerNewRatingMap.forEach((key, value) -> Logger.debug("(μ)={} (σ)={} {}", value.getMean(), value.getStandardDeviation(), key));

            List<Rating> newRatingsForPlayer = playerNewRatingMap.entrySet().stream()
                    .filter(e -> ((OrmLitePlayer) e.getKey()).getName().equals(player)).map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            Rating newRating;
            if (newRatingsForPlayer.size() > 1) {
                Logger.debug("Multiple new ratings, combining ratings");
                newRating = calculatorService.combine(newRatingsForPlayer);
            } else {
                Logger.debug("No need to combine new rating");
                newRating = newRatingsForPlayer.get(0);
            }
            Rating oldRating = new Rating(match.getPlayerMean().doubleValue(), match.getPlayerStdDev().doubleValue());
            Logger.debug("Ratings for {}", player);
            Logger.debug("(μ): last={}, old={}, new={}", lastRating.getMean(), oldRating.getMean(), newRating.getMean());
            Logger.debug("(σ): last={}, old={}, new={}", lastRating.getStandardDeviation(), oldRating.getStandardDeviation(), newRating.getStandardDeviation());

            boolean meanMatch = Math.abs(oldRating.getMean() - newRating.getMean()) < 0.00001;
            boolean stdevMatch = Math.abs(oldRating.getStandardDeviation() - newRating.getStandardDeviation()) < 0.00001;
            if (meanMatch && stdevMatch) {
                Logger.debug("<= Rating for {} are correct", player);
            } else {
                Logger.debug("<= Bad rating for {}, updating database", player);
                match.setPlayerMean(BigDecimal.valueOf(newRating.getMean()));
                match.setPlayerStdDev(BigDecimal.valueOf(newRating.getStandardDeviation()));
                try {
                    playDao.update(match);
                } catch (final SQLException e) {
                    Logger.error(e);
                }
            }
        });
    }

    @SneakyThrows
    public List<PlayEntity> retrieveAllMatches() {
        return playDao.queryBuilder().orderBy(PlayEntity.PLAY_DATE, true).query();
    }

    @SneakyThrows
    public List<PlayEntity> retrieveAllMatchesForGame(final OrmLiteGame ormLiteGame) {
        return playDao.queryBuilder()
                .orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(PlayEntity.GAME_NAME, ormLiteGame.getName()).query();
    }

    @SneakyThrows
    public List<PlayEntity> retrieveAllMatchesForGameAndPlayer(final OrmLiteGame ormLiteGame, final OrmLitePlayer ormLitePlayer) {
        return playDao.queryBuilder().orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(PlayEntity.GAME_NAME, ormLiteGame)
                .and().eq(PlayEntity.PLAYER_NAME, ormLitePlayer)
                .query();
    }

    @SneakyThrows
    public List<PlayEntity> retrieveAllMatchesForPlayer(final OrmLitePlayer ormLitePlayer) {
        return playDao.queryBuilder()
                .orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(PlayEntity.PLAYER_NAME, ormLitePlayer.getName())
                .query();
    }

    @SneakyThrows
    public List<PlayEntity> retrieveLastFewMatches(final Long limit) {
        List<PlayEntity> ormLiteMatchList = new ArrayList<>();
        for (final PlayEntity ormLiteMatch : playDao.queryBuilder()
                .groupBy(PlayEntity.SESSION_ID).limit(limit).orderBy(PlayEntity.PLAY_DATE, false).query()) {
            String sessionId = ormLiteMatch.getSessionId();
            ormLiteMatchList.addAll(playDao.queryBuilder().where().eq(PlayEntity.SESSION_ID, sessionId).query());
        }
        return ormLiteMatchList;
    }

    @SneakyThrows
    public List<PlayEntity> retrieveLastFewMatchesForGame(final OrmLiteGame ormLiteGame, final Long limit) {
        List<PlayEntity> ormLiteMatchList = new ArrayList<>();
        for (final PlayEntity ormLiteMatch : playDao.queryBuilder()
                .groupBy(PlayEntity.SESSION_ID).limit(limit).orderBy(OrmLiteMatch.PLAY_DATE, false)
                .where().eq(PlayEntity.GAME_NAME, ormLiteGame.getName()).query()) {
            String sessionId = ormLiteMatch.getSessionId();
            ormLiteMatchList.addAll(playDao.queryBuilder().where().eq(PlayEntity.SESSION_ID, sessionId).query());
        }
        return ormLiteMatchList;
    }

    @SneakyThrows
    public List<PlayEntity> retrieveLastFewMatchesForGameAndPlayer(final OrmLiteGame ormLiteGame, final OrmLitePlayer ormLitePlayer, final Long limit) {
        List<PlayEntity> ormLiteMatchList = new ArrayList<>();
        for (final PlayEntity ormLiteMatch : playDao.queryBuilder()
                .limit(limit).orderBy(PlayEntity.PLAY_DATE, false)
                .groupBy(PlayEntity.SESSION_ID)
                .where().eq(PlayEntity.GAME_NAME, ormLiteGame)
                .and().eq(PlayEntity.PLAYER_NAME, ormLitePlayer).query()) {
            String sessionId = ormLiteMatch.getSessionId();
            ormLiteMatchList.addAll(playDao.queryBuilder().where().eq(PlayEntity.SESSION_ID, sessionId).query());
        }
        Logger.debug("game={}, player={}, matches={}", ormLiteGame.getName(), ormLitePlayer.getName(), ormLiteMatchList.stream().map(m -> m.getSessionId().substring(0, m.getSessionId().indexOf("-"))).collect(Collectors.joining(";")));
        return ormLiteMatchList;
    }

    @SneakyThrows
    public List<PlayEntity> retrieveLastFewMatchesForPlayer(final OrmLitePlayer ormLitePlayer, final Long limit) {
        List<PlayEntity> ormLiteMatchList = new ArrayList<>();
        for (final PlayEntity ormLiteMatch : playDao.queryBuilder()
                .groupBy(PlayEntity.SESSION_ID).limit(limit).orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(PlayEntity.PLAYER_NAME, ormLitePlayer)
                .query()) {
            String sessionId = ormLiteMatch.getSessionId();
            ormLiteMatchList.addAll(playDao.queryBuilder().where().eq(PlayEntity.SESSION_ID, sessionId).query());
        }
        return ormLiteMatchList;
    }

    @SneakyThrows
    public PlayEntity retrieveLastPlayerMatchForGame(final OrmLiteGame ormLiteGame, final OrmLitePlayer ormLitePlayer) {
        if (ormLiteGame == null) {
            Logger.debug("Game is not found");
            return null;
        }

        if (ormLitePlayer.getName() == null) {
            Logger.debug("{} does not exist in database", ormLitePlayer);
            return null;
        }
        Logger.trace("=> retrieve last player match for game={}, player={}", ormLiteGame.getName(), ormLitePlayer.getName());

        List<PlayEntity> ormLiteMatchList = playDao.queryBuilder().orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(PlayEntity.GAME_NAME, ormLiteGame.getName())
                .and().eq(PlayEntity.PLAYER_NAME, ormLitePlayer.getName())
                .query();

        PlayEntity ormLiteMatch = collapseMatchTable(ormLiteGame, ormLitePlayer, ormLiteMatchList);
        Logger.trace("<= game={}, player={}, matches={}", ormLiteGame.getName(), ormLitePlayer.getName(), ormLiteMatchList.stream().map(PlayEntity::getSessionId).collect(Collectors.joining(";")));
        return ormLiteMatch;
    }

    @SneakyThrows
    public PlayEntity retrieveLastPlayerMatchForGameBefore(final Date playDate, final OrmLiteGame ormLiteGame, final OrmLitePlayer ormLitePlayer) {
        Logger.trace("=> retrieving last {} game played by {} before {}", ormLiteGame.getName(), ormLitePlayer.getName(), playDate.getTime());
        List<PlayEntity> ormLiteMatchList = playDao.queryBuilder()
                .orderBy(PlayEntity.PLAY_DATE, false)
                .where().eq(PlayEntity.GAME_NAME, ormLiteGame.getName())
                .and().eq(PlayEntity.PLAYER_NAME, ormLitePlayer.getName())
                .and().lt(PlayEntity.PLAY_DATE, playDate)
                .query();
        PlayEntity ormLiteMatch = collapseMatchTable(ormLiteGame, ormLitePlayer, ormLiteMatchList);
        Logger.trace("<= game={}, player={}, matches={}", ormLiteGame.getName(), ormLitePlayer.getName(), ormLiteMatchList.stream().map(PlayEntity::getSessionId).collect(Collectors.joining(";")));
        return ormLiteMatch;
    }

    @SneakyThrows
    public List<PlayEntity> retrieveMatchWithSessionId(final String id) {
        String sessionId = String.format("%s%%", id);
        return playDao.queryBuilder().where().like(PlayEntity.SESSION_ID, sessionId).query();
    }

    @SneakyThrows
    public Rating retrieveRatingBefore(final Date playDate, final OrmLitePlayer ormLitePlayer, final OrmLiteGame ormLiteGame) {
        PlayEntity ormLiteMatch = retrieveLastPlayerMatchForGameBefore(playDate, ormLiteGame, ormLitePlayer);
        Logger.debug("Last rating (μ)={} (σ)={} {}", ormLiteMatch.getPlayerMean(), ormLiteMatch.getPlayerStdDev(), ormLiteMatch.getPlayerName());
        return matchMapper.toRating(ormLiteMatch);
    }

    @SneakyThrows
    public List<PlayEntity> retrieveTopPlayerMatchesForGame(final OrmLiteGame ormLiteGame, final Long limit) {
        List<PlayEntity> allMatchesForGame = retrieveAllMatchesForGame(ormLiteGame);

        Map<String, PlayEntity> playerMatchMap = new HashMap<>();
        allMatchesForGame.forEach(matchTable -> playerMatchMap.putIfAbsent(matchTable.getPlayerName(), matchTable));

        return playerMatchMap.values().stream()
                .sorted(Comparator.comparing((PlayEntity m) -> m.getPlayerMean().subtract(m.getPlayerStdDev().multiply(BigDecimal.valueOf(3)))).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

}
