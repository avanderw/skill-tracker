package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import de.gesundkrank.jskills.*;
import lombok.SneakyThrows;
import net.avdw.skilltracker.calculator.CalculatorService;
import net.avdw.skilltracker.game.GameMapper;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerTable;
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
    private final Dao<MatchTable, Integer> matchTableDao;
    private final SkillCalculator skillCalculator;

    @Inject
    MatchService(final Dao<MatchTable, Integer> matchTableDao,
                 final SkillCalculator skillCalculator,
                 final MatchMapper matchMapper,
                 final GameMapper gameMapper,
                 final CalculatorService calculatorService,
                 final MatchDataBuilder matchDataBuilder) {
        this.matchTableDao = matchTableDao;
        this.skillCalculator = skillCalculator;
        this.matchMapper = matchMapper;
        this.gameMapper = gameMapper;
        this.calculatorService = calculatorService;
        this.matchDataBuilder = matchDataBuilder;
    }

    public BigDecimal calculateMatchQuality(final GameTable gameTable, final List<ITeam> teams) {
        GameInfo gameInfo = gameMapper.toGameInfo(gameTable);
        return BigDecimal.valueOf(skillCalculator.calculateMatchQuality(gameInfo, teams));
    }

    private MatchTable collapseMatchTable(final GameTable gameTable, final PlayerTable playerTable, final List<MatchTable> matchTableList) {
        MatchTable matchTable;
        if (matchTableList.isEmpty()) {
            Logger.debug("{} does not appear in match", playerTable.getName());
            matchTable = matchMapper.toMatchTable(gameTable, playerTable, gameMapper.toRating(gameTable));
        } else if (matchTableList.size() == 1) {
            matchTable = matchTableList.get(0);
        } else {
            Date lastPlayed = matchTableList.get(0).getPlayDate();
            Map<Date, List<MatchTable>> dateMatchTableList = matchTableList.stream().collect(Collectors.groupingBy(MatchTable::getPlayDate));
            if (dateMatchTableList.get(lastPlayed).size() == 1) {
                Logger.debug("{} appears once in match", playerTable.getName());
                matchTable = dateMatchTableList.get(lastPlayed).get(0);
            } else {
                Logger.debug("{} appears twice in match, returning average of means & stdevs", playerTable.getName());
                matchTable = matchMapper.toMatchTable(dateMatchTableList.get(lastPlayed).get(0));
                matchTable.setMean(BigDecimal.valueOf(dateMatchTableList.get(lastPlayed).stream().mapToDouble(m -> m.getMean().doubleValue()).average().orElseThrow(UnsupportedOperationException::new)));
                matchTable.setStandardDeviation(BigDecimal.valueOf(dateMatchTableList.get(lastPlayed).stream().mapToDouble(m -> m.getStandardDeviation().doubleValue()).average().orElseThrow(UnsupportedOperationException::new)));
            }
        }
        return matchTable;
    }

    @SneakyThrows
    public void combinePlayer(final PlayerTable fromPlayer, final PlayerTable toPlayer) {
        UpdateBuilder<MatchTable, Integer> preparedUpdate = matchTableDao.updateBuilder();
        preparedUpdate.updateColumnValue(MatchTable.PLAYER_FK, toPlayer.getPk())
                .where().eq(MatchTable.PLAYER_FK, fromPlayer.getPk());
        preparedUpdate.update();

        recalculate(toPlayer);
    }

    @SneakyThrows
    public List<MatchTable> createMatchForGame(final GameTable gameTable, final List<ITeam> teams, final int... ranks) {
        Logger.debug("create match for game={}, ranks={}, teams={}", gameTable.getName(), Arrays.toString(ranks), teams);
        String sessionId = UUID.randomUUID().toString();
        Date date = new Date();
        GameInfo gameInfo = gameMapper.toGameInfo(gameTable);
        Map<IPlayer, Rating> newRatings = skillCalculator.calculateNewRatings(gameInfo, teams, Arrays.copyOf(ranks, ranks.length));
        List<MatchTable> matchTableList = new ArrayList<>();
        newRatings.forEach((p, r) -> {
            PlayerTable playerTable = (PlayerTable) p;

            MatchTable matchTable = matchMapper.toMatchTable(gameTable, playerTable, r);
            matchTable.setSessionId(sessionId);
            matchTable.setPlayDate(date);
            int teamIdx = -1;
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).keySet().stream().anyMatch(key -> key.equals(playerTable))) {
                    Logger.debug("old {}={}", playerTable.getName(), teams.get(i).get(playerTable));
                    Logger.debug("new {}={}", playerTable.getName(), r);
                    teamIdx = i;
                    break;
                }
            }

            matchTable.setTeam(teamIdx);
            matchTable.setRank(ranks[teamIdx]);
            matchTableList.add(matchTable);
        });
        matchTableDao.create(matchTableList);
        return matchTableList;
    }

    @SneakyThrows
    public boolean deleteMatch(final String partial) {
        List<MatchTable> matchList = matchTableDao.queryBuilder().where().like(MatchTable.SESSION_ID, String.format("%s%%", partial)).query();
        List<PlayerTable> playerList = matchList.stream().map(MatchTable::getPlayerTable).collect(Collectors.toList());
        boolean deleted = matchTableDao.delete(matchList) > 0;
        if (deleted) {
            playerList.forEach(this::recalculate);
        }
        return deleted;
    }

    @SneakyThrows
    public List<GameTable> gameListForPlayer(final PlayerTable playerTable) {
        return matchTableDao.queryBuilder().groupBy(MatchTable.GAME_FK)
                .where().eq(MatchTable.PLAYER_FK, playerTable).query().stream()
                .map(MatchTable::getGameTable)
                .collect(Collectors.toList());
    }

    public void recalculate(final PlayerTable player) {
        Logger.trace("=> recalculate {}", player.getName());
        List<MatchTable> matchList = retrieveAllMatchesForPlayer(player);
        matchList.stream().map(m -> m.getPlayDate().getTime()).forEach(l -> Logger.debug("session={}", l));
        matchList.stream().sorted(Comparator.comparing(MatchTable::getPlayDate)).forEach(match -> {
            Logger.debug("recalculate => player={}, session={}", player.getName(), match.getSessionId());
            List<MatchTable> sessionList = retrieveMatchWithSessionId(match.getSessionId());
            Date playDate = sessionList.stream().findAny().orElseThrow().getPlayDate();
            Logger.debug("recalculate => playDate={}", playDate.getTime());
            GameTable gameTable = sessionList.stream().findAny().orElseThrow().getGameTable();

            Logger.debug("recalculate => {}", player.getName());
            Rating lastRating = retrieveRatingBefore(playDate, player, gameTable);
            Logger.debug("lastRating => {}", lastRating);
            Map<String, Map<String, Rating>> lastPlayerListGameListRatingMap = new HashMap<>();
            MatchData matchData = matchDataBuilder.buildFromMatchTable(sessionList);
            matchData.getTeamDataSet().forEach(team -> team.getPlayerTableSet().forEach(p -> {
                Logger.debug("recalculate => team player {}", p.getName());
                lastPlayerListGameListRatingMap.putIfAbsent(p.getName(), new HashMap<>());
                lastPlayerListGameListRatingMap.get(p.getName()).putIfAbsent(gameTable.getName(), retrieveRatingBefore(playDate, p, gameTable));
            }));

            lastPlayerListGameListRatingMap.forEach((k, v) -> Logger.debug("lastPlayerListGameListRatingMap => {}={}", k, v));
            Map<IPlayer, Rating> playerNewRatingMap = calculatorService.calculate(sessionList, lastPlayerListGameListRatingMap);
            Logger.debug("New ratings for {}", gameTable.getName());
            playerNewRatingMap.forEach((key, value) -> Logger.debug("(μ)={} (σ)={} {}", value.getMean(), value.getStandardDeviation(), key));

            List<Rating> newRatingsForPlayer = playerNewRatingMap.entrySet().stream()
                    .filter(e -> ((PlayerTable) e.getKey()).getName().equals(player.getName())).map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            Rating newRating;
            if (newRatingsForPlayer.size() > 1) {
                Logger.debug("Multiple new ratings, combining ratings");
                newRating = calculatorService.combine(newRatingsForPlayer);
            } else {
                Logger.debug("No need to combine new rating");
                newRating = newRatingsForPlayer.get(0);
            }
            Rating oldRating = new Rating(match.getMean().doubleValue(), match.getStandardDeviation().doubleValue());
            Logger.debug("Ratings for {}", player.getName());
            Logger.debug("(μ): last={}, old={}, new={}", lastRating.getMean(), oldRating.getMean(), newRating.getMean());
            Logger.debug("(σ): last={}, old={}, new={}", lastRating.getStandardDeviation(), oldRating.getStandardDeviation(), newRating.getStandardDeviation());

            boolean meanMatch = Math.abs(oldRating.getMean() - newRating.getMean()) < 0.00001;
            boolean stdevMatch = Math.abs(oldRating.getStandardDeviation() - newRating.getStandardDeviation()) < 0.00001;
            if (meanMatch && stdevMatch) {
                Logger.debug("<= Rating for {} are correct", player.getName());
            } else {
                Logger.debug("<= Bad rating for {}, updating database", player.getName());
                match.setMean(BigDecimal.valueOf(newRating.getMean()));
                match.setStandardDeviation(BigDecimal.valueOf(newRating.getStandardDeviation()));
                try {
                    matchTableDao.update(match);
                } catch (final SQLException e) {
                    Logger.error(e);
                }
            }
        });
    }

    @SneakyThrows
    public List<MatchTable> retrieveAllMatches() {
        return matchTableDao.queryBuilder().orderBy(MatchTable.PLAY_DATE, true).query();
    }

    @SneakyThrows
    public List<MatchTable> retrieveAllMatchesForGame(final GameTable gameTable) {
        return matchTableDao.queryBuilder()
                .orderBy(MatchTable.PLAY_DATE, false)
                .where().eq(MatchTable.GAME_FK, gameTable).query();
    }

    @SneakyThrows
    public List<MatchTable> retrieveAllMatchesForGameAndPlayer(final GameTable gameTable, final PlayerTable playerTable) {
        return matchTableDao.queryBuilder().orderBy(MatchTable.PLAY_DATE, false)
                .where().eq(MatchTable.GAME_FK, gameTable)
                .and().eq(MatchTable.PLAYER_FK, playerTable)
                .query();
    }

    @SneakyThrows
    public List<MatchTable> retrieveAllMatchesForPlayer(final PlayerTable playerTable) {
        return matchTableDao.queryBuilder()
                .orderBy(MatchTable.PLAY_DATE, false)
                .where().eq(MatchTable.PLAYER_FK, playerTable.getPk())
                .query();
    }

    @SneakyThrows
    public List<MatchTable> retrieveLastFewMatches(final Long limit) {
        List<MatchTable> matchTableList = new ArrayList<>();
        for (final MatchTable matchTable : matchTableDao.queryBuilder()
                .groupBy(MatchTable.SESSION_ID).limit(limit).orderBy(MatchTable.PLAY_DATE, false).query()) {
            String sessionId = matchTable.getSessionId();
            matchTableList.addAll(matchTableDao.queryBuilder().where().eq(MatchTable.SESSION_ID, sessionId).query());
        }
        return matchTableList;
    }

    @SneakyThrows
    public List<MatchTable> retrieveLastFewMatchesForGame(final GameTable gameTable, final Long limit) {
        List<MatchTable> matchTableList = new ArrayList<>();
        for (final MatchTable matchTable : matchTableDao.queryBuilder()
                .groupBy(MatchTable.SESSION_ID).limit(limit).orderBy(MatchTable.PLAY_DATE, false)
                .where().eq(MatchTable.GAME_FK, gameTable).query()) {
            String sessionId = matchTable.getSessionId();
            matchTableList.addAll(matchTableDao.queryBuilder().where().eq(MatchTable.SESSION_ID, sessionId).query());
        }
        return matchTableList;
    }

    @SneakyThrows
    public List<MatchTable> retrieveLastFewMatchesForGameAndPlayer(final GameTable gameTable, final PlayerTable playerTable, final Long limit) {
        List<MatchTable> matchTableList = new ArrayList<>();
        for (final MatchTable matchTable : matchTableDao.queryBuilder()
                .limit(limit).orderBy(MatchTable.PLAY_DATE, false)
                .groupBy(MatchTable.SESSION_ID)
                .where().eq(MatchTable.GAME_FK, gameTable)
                .and().eq(MatchTable.PLAYER_FK, playerTable).query()) {
            String sessionId = matchTable.getSessionId();
            matchTableList.addAll(matchTableDao.queryBuilder().where().eq(MatchTable.SESSION_ID, sessionId).query());
        }
        Logger.debug("game={}, player={}, matches={}", gameTable.getName(), playerTable.getName(), matchTableList.stream().map(m -> m.getSessionId().substring(0, m.getSessionId().indexOf("-"))).collect(Collectors.joining(";")));
        return matchTableList;
    }

    @SneakyThrows
    public List<MatchTable> retrieveLastFewMatchesForPlayer(final PlayerTable playerTable, final Long limit) {
        List<MatchTable> matchTableList = new ArrayList<>();
        for (final MatchTable matchTable : matchTableDao.queryBuilder()
                .groupBy(MatchTable.SESSION_ID).limit(limit).orderBy(MatchTable.PLAY_DATE, false)
                .where().eq(MatchTable.PLAYER_FK, playerTable)
                .query()) {
            String sessionId = matchTable.getSessionId();
            matchTableList.addAll(matchTableDao.queryBuilder().where().eq(MatchTable.SESSION_ID, sessionId).query());
        }
        return matchTableList;
    }

    @SneakyThrows
    public MatchTable retrieveLastPlayerMatchForGame(final GameTable gameTable, final PlayerTable playerTable) {
        if (gameTable == null) {
            Logger.debug("Game is not found");
            return null;
        }

        if (playerTable.getPk() == null) {
            Logger.debug("Player does not exist in database");
            return null;
        }
        Logger.trace("=> retrieve last player match for game={}, player={}", gameTable.getName(), playerTable.getName());

        List<MatchTable> matchTableList = matchTableDao.queryBuilder().orderBy(MatchTable.PLAY_DATE, false)
                .where().eq(MatchTable.GAME_FK, gameTable)
                .and().eq(MatchTable.PLAYER_FK, playerTable)
                .query();

        MatchTable matchTable = collapseMatchTable(gameTable, playerTable, matchTableList);
        Logger.trace("<= game={}, player={}, matches={}", gameTable.getName(), playerTable.getName(), matchTableList.stream().map(MatchTable::getSessionId).collect(Collectors.joining(";")));
        return matchTable;
    }

    @SneakyThrows
    public MatchTable retrieveLastPlayerMatchForGameBefore(final Date playDate, final GameTable gameTable, final PlayerTable playerTable) {
        Logger.trace("=> retrieving last {} game played by {} before {}", gameTable.getName(), playerTable.getName(), playDate.getTime());
        List<MatchTable> matchTableList = matchTableDao.queryBuilder()
                .orderBy(MatchTable.PLAY_DATE, false)
                .where().eq(MatchTable.GAME_FK, gameTable)
                .and().eq(MatchTable.PLAYER_FK, playerTable)
                .and().lt(MatchTable.PLAY_DATE, playDate)
                .query();
        MatchTable matchTable = collapseMatchTable(gameTable, playerTable, matchTableList);
        Logger.trace("<= game={}, player={}, matches={}", gameTable.getName(), playerTable.getName(), matchTableList.stream().map(MatchTable::getSessionId).collect(Collectors.joining(";")));
        return matchTable;
    }

    @SneakyThrows
    public List<MatchTable> retrieveMatchWithSessionId(final String id) {
        String sessionId = String.format("%s%%", id);
        return matchTableDao.queryBuilder().where().like(MatchTable.SESSION_ID, sessionId).query();
    }

    @SneakyThrows
    public Rating retrieveRatingBefore(final Date playDate, final PlayerTable playerTable, final GameTable gameTable) {
        MatchTable matchTable = retrieveLastPlayerMatchForGameBefore(playDate, gameTable, playerTable);
        Logger.debug("Last rating (μ)={} (σ)={} {}", matchTable.getMean(), matchTable.getStandardDeviation(), matchTable.getPlayerTable().getName());
        return matchMapper.toRating(matchTable);
    }

    @SneakyThrows
    public List<MatchTable> retrieveTopPlayerMatchesForGame(final GameTable gameTable, final Long limit) {
        List<MatchTable> allMatchesForGame = retrieveAllMatchesForGame(gameTable);

        Map<String, MatchTable> playerMatchMap = new HashMap<>();
        allMatchesForGame.forEach(matchTable -> playerMatchMap.putIfAbsent(matchTable.getPlayerTable().getName(), matchTable));

        return playerMatchMap.values().stream()
                .sorted(Comparator.comparing((MatchTable m) -> m.getMean().subtract(m.getStandardDeviation().multiply(BigDecimal.valueOf(3)))).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
