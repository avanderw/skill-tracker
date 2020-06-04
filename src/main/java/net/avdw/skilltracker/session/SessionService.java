package net.avdw.skilltracker.session;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import de.gesundkrank.jskills.*;
import net.avdw.skilltracker.game.GameMapper;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerTable;
import org.tinylog.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SessionService {
    private final Dao<SessionTable, Integer> sessionTableDao;
    private final SkillCalculator skillCalculator;
    private final SessionMapper sessionMapper;
    private final GameMapper gameMapper;

    @Inject
    SessionService(final Dao<SessionTable, Integer> sessionTableDao,
                   final SkillCalculator skillCalculator,
                   final SessionMapper sessionMapper,
                   final GameMapper gameMapper) {
        this.sessionTableDao = sessionTableDao;
        this.skillCalculator = skillCalculator;
        this.sessionMapper = sessionMapper;
        this.gameMapper = gameMapper;
    }

    public void createSessionForGame(final GameTable gameTable, final List<ITeam> teams, final int... ranks) {
        String sessionId = UUID.randomUUID().toString();
        GameInfo gameInfo = gameMapper.map(gameTable);
        Map<IPlayer, Rating> newRatings = skillCalculator.calculateNewRatings(gameInfo, teams, ranks);
        List<SessionTable> sessionTableList = new ArrayList<>();
        newRatings.forEach((p, r) -> {
            PlayerTable playerTable = (PlayerTable) p;
            Logger.debug(String.format("%s (%s)", r, playerTable.getName()));
            SessionTable sessionTable = sessionMapper.map(gameTable, playerTable, r);
            sessionTable.setSessionId(sessionId);
            int teamIdx = -1;
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).containsKey(playerTable)) {
                    teamIdx = i;
                    break;
                }
            }
            sessionTable.setTeam(teamIdx);
            sessionTable.setRank(ranks[teamIdx]);
            sessionTableList.add(sessionTable);
        });
        try {
            sessionTableDao.create(sessionTableList);
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public SessionTable retrieveLatestPlayerSessionForGame(final GameTable game, final PlayerTable playerTable) {
        try {
            return sessionTableDao.queryBuilder().orderBy(SessionTable.PLAY_DATE, false).where()
                    .eq(SessionTable.GAME_FK, game.getPk())
                    .and().eq(SessionTable.PLAYER_FK, playerTable.getPk())
                    .queryForFirst();
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
