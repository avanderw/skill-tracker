package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import de.gesundkrank.jskills.*;
import net.avdw.skilltracker.game.GameMapper;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerTable;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MatchService {
    private final Dao<MatchTable, Integer> matchTableDao;
    private final SkillCalculator skillCalculator;
    private final MatchMapper matchMapper;
    private final GameMapper gameMapper;

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

    public void createSessionForGame(final GameTable gameTable, final List<ITeam> teams, final int... ranks) {
        String sessionId = UUID.randomUUID().toString();
        GameInfo gameInfo = gameMapper.map(gameTable);
        Map<IPlayer, Rating> newRatings = skillCalculator.calculateNewRatings(gameInfo, teams, ranks);
        List<MatchTable> matchTableList = new ArrayList<>();
        newRatings.forEach((p, r) -> {
            PlayerTable playerTable = (PlayerTable) p;
            Logger.debug(String.format("%s (%s)", r, playerTable.getName()));
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
        try {
            matchTableDao.create(matchTableList);
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public MatchTable retrieveLatestPlayerSessionForGame(final GameTable game, final PlayerTable playerTable) {
        try {
            return matchTableDao.queryBuilder().orderBy(MatchTable.PLAY_DATE, false).where()
                    .eq(MatchTable.GAME_FK, game.getPk())
                    .and().eq(MatchTable.PLAYER_FK, playerTable.getPk())
                    .queryForFirst();
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public List<MatchTable> retrieveAllMatchesForPlayer(final PlayerTable playerTable) {
        try {
            return matchTableDao.queryForEq(MatchTable.PLAYER_FK, playerTable.getPk());
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public List<MatchTable> retrieveAllMatchesForGame(final GameTable gameTable) {
        try {
            return matchTableDao.queryForEq(MatchTable.GAME_FK, gameTable.getPk());
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public BigDecimal calculateMatchQuality(final GameTable gameTable, final List<ITeam> teams) {
        GameInfo gameInfo = gameMapper.map(gameTable);
        return BigDecimal.valueOf(skillCalculator.calculateMatchQuality(gameInfo, teams));
    }
}
