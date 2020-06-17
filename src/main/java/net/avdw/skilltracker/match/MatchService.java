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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    public void createMatchForGame(final GameTable gameTable, final List<ITeam> teams, final int... ranks) {
        String sessionId = UUID.randomUUID().toString();
        GameInfo gameInfo = gameMapper.toGameInfo(gameTable);
        Map<IPlayer, Rating> newRatings = skillCalculator.calculateNewRatings(gameInfo, teams, ranks);
        List<MatchTable> matchTableList = new ArrayList<>();
        newRatings.forEach((p, r) -> {
            PlayerTable playerTable = (PlayerTable) p;
            Logger.debug(String.format("%s (%s)", playerTable.getName(), r));
            MatchTable matchTable = matchMapper.map(gameTable, playerTable, r);
            matchTable.setSessionId(sessionId);
            int teamIdx = -1;
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).containsKey(playerTable)) {
                    teamIdx = i;
                    break;
                }
            }
            matchTable.setTeam(teamIdx);
            matchTable.setRank(ranks[teamIdx]);
            matchTableList.add(matchTable);
        });
        matchTableDao.create(matchTableList);
    }

    @SneakyThrows
    public Map<String, List<MatchTable>> retrieveAllMatchesForGame(final GameTable gameTable) {
        return matchTableDao.queryForEq(MatchTable.GAME_FK, gameTable.getPk()).stream().collect(Collectors.groupingBy(MatchTable::getSessionId));
    }

    @SneakyThrows
    public List<MatchTable> retrieveAllMatchesForPlayer(final PlayerTable playerTable) {
        return matchTableDao.queryForEq(MatchTable.PLAYER_FK, playerTable.getPk());
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
            matchTable = matchMapper.map(gameTable, playerTable, gameMapper.toRating(gameTable));
        } else {
            Logger.trace("Matches for {} {}", playerTable, matchTableList);
            matchTable = matchTableList.get(0);
        }

        Logger.debug("Latest match for game {} {}", gameTable, matchTable);
        return matchTable;
    }
}
