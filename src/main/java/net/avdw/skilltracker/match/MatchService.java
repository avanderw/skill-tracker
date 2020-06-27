package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import de.gesundkrank.jskills.*;
import lombok.SneakyThrows;
import net.avdw.skilltracker.game.GameMapper;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerTable;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class MatchService {
    private final GameMapper gameMapper;
    private final MatchMapper matchMapper;
    private final Dao<MatchTable, Integer> matchTableDao;
    private final SkillCalculator skillCalculator;

    @Inject
    MatchService(final Dao<MatchTable, Integer> matchTableDao,
                 final SkillCalculator skillCalculator,
                 final MatchMapper matchMapper,
                 final GameMapper gameMapper) {
        this.matchTableDao = matchTableDao;
        this.skillCalculator = skillCalculator;
        this.matchMapper = matchMapper;
        this.gameMapper = gameMapper;
    }

    public BigDecimal calculateMatchQuality(final GameTable gameTable, final List<ITeam> teams) {
        GameInfo gameInfo = gameMapper.toGameInfo(gameTable);
        return BigDecimal.valueOf(skillCalculator.calculateMatchQuality(gameInfo, teams));
    }

    @SneakyThrows
    public List<MatchTable> createMatchForGame(final GameTable gameTable, final List<ITeam> teams, final int... ranks) {
        Logger.debug("Create match for game GAME={}, TEAMS={}, RANKS={}", gameTable, teams, Arrays.toString(ranks));
        String sessionId = UUID.randomUUID().toString();
        GameInfo gameInfo = gameMapper.toGameInfo(gameTable);
        Map<IPlayer, Rating> newRatings = skillCalculator.calculateNewRatings(gameInfo, teams, Arrays.copyOf(ranks, ranks.length));
        List<MatchTable> matchTableList = new ArrayList<>();
        newRatings.forEach((p, r) -> {
            PlayerTable playerTable = (PlayerTable) p;
            Logger.debug(String.format("new Ratings for %s = (%s)", playerTable.getName(), r));

            MatchTable matchTable = matchMapper.toMatchTable(gameTable, playerTable, r);
            matchTable.setSessionId(sessionId);
            int teamIdx = -1;
            for (int i = 0; i < teams.size(); i++) {
                Logger.trace("Test {} == {}", teams.get(i), playerTable);

                if (teams.get(i).keySet().stream().anyMatch(key -> key.equals(playerTable))) {
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
        return matchTableDao.delete(matchTableDao.queryBuilder().where().like(MatchTable.SESSION_ID, String.format("%s%%", partial)).query()) > 0;
    }

    @SneakyThrows
    public Map<String, List<MatchTable>> retrieveAllMatchesForGame(final GameTable gameTable) {
        return matchTableDao.queryForEq(MatchTable.GAME_FK, gameTable.getPk()).stream().collect(Collectors.groupingBy(MatchTable::getSessionId));
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
                .where().eq(MatchTable.GAME_FK, gameTable)
                .and().eq(MatchTable.PLAYER_FK, playerTable).query()) {
            String sessionId = matchTable.getSessionId();
            matchTableList.addAll(matchTableDao.queryBuilder().where().eq(MatchTable.SESSION_ID, sessionId).query());
        }
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
    public MatchTable retrieveLatestPlayerMatchForGame(final GameTable gameTable, final PlayerTable playerTable) {
        Logger.trace("Retrieve latest player match for {}", playerTable);

        if (playerTable.getPk() == null) {
            Logger.debug("Player does not exist in database");
            return null;
        }

        List<MatchTable> matchTableList = matchTableDao.queryBuilder().orderBy(MatchTable.PLAY_DATE, false).where()
                .eq(MatchTable.GAME_FK, gameTable)
                .and().eq(MatchTable.PLAYER_FK, playerTable)
                .query();

        MatchTable matchTable;
        if (matchTableList.isEmpty()) {
            matchTable = matchMapper.toMatchTable(gameTable, playerTable, gameMapper.toRating(gameTable));
        } else {
            Logger.trace("Matches for {} {}", playerTable, matchTableList);
            matchTable = matchTableList.get(0);
        }

        Logger.debug("Latest match for game {} {}", gameTable, matchTable);
        return matchTable;
    }

    @SneakyThrows
    public List<MatchTable> retrieveMatchWithSessionId(final String id) {
        String sessionId = String.format("%s%%", id);
        return matchTableDao.queryBuilder().where().like(MatchTable.SESSION_ID, sessionId).query();
    }
}
