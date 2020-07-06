package net.avdw.skilltracker.calculator;

import com.google.inject.Inject;
import de.gesundkrank.jskills.*;
import net.avdw.skilltracker.game.GameMapper;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.match.MatchData;
import net.avdw.skilltracker.match.MatchDataBuilder;
import net.avdw.skilltracker.match.MatchTable;
import net.avdw.skilltracker.match.PlayerRankingMap;
import net.avdw.skilltracker.player.PlayerTable;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class CalculatorService {
    private final GameMapper gameMapper;
    private final MatchDataBuilder matchDataBuilder;
    private final SkillCalculator skillCalculator;

    @Inject
    CalculatorService(final GameMapper gameMapper,
                      final MatchDataBuilder matchDataBuilder,
                      final SkillCalculator skillCalculator) {
        this.gameMapper = gameMapper;
        this.matchDataBuilder = matchDataBuilder;
        this.skillCalculator = skillCalculator;
    }

    private int[] buildRanks(final List<ITeam> teamList, final List<MatchTable> sessionMatchTableList) {
        Logger.debug("-> build ranks");
        sessionMatchTableList.forEach(m->Logger.debug("(≻)={} (μ)={} (σ)={} {}", m.getRank(), m.getMean(), m.getStandardDeviation(), m.getPlayerTable().getName()));
        int[] ranks = new int[teamList.size()];
        List<MatchTable> matchTableList = new ArrayList<>(sessionMatchTableList);
        matchTableList.sort(Comparator.comparingInt(MatchTable::getRank));
        for (int i = teamList.size(); i > 0; i--) {
            ITeam team = teamList.get(i - 1);
            String name = ((PlayerTable) team.keySet().stream().findAny().orElseThrow()).getName();

            for (int j = matchTableList.size(); j > 0; j--) {
                MatchTable m = matchTableList.get(j - 1);
                if (m.getPlayerTable().getName().equals(name)) {
                    ranks[i - 1] = m.getRank();
                    matchTableList.remove(m);
                    matchTableList.forEach(Logger::debug);
                    break;
                }
            }
        }
        Logger.debug("<- build ranks {}", Arrays.toString(ranks));
        return ranks;
    }

    public Map<IPlayer, Rating> calculate(final List<MatchTable> sessionList, final Map<String, Map<String, Rating>> lastPlayerRating) {
        Logger.trace("=> calculate");
        sessionList.forEach(session -> Logger.debug("(μ)={} (σ)={} (≻)={} {}", session.getMean(), session.getStandardDeviation(), session.getRank(), session.getPlayerTable().getName()));
        lastPlayerRating.entrySet().forEach(Logger::debug);
        GameTable gameTable = sessionList.stream().findAny().orElseThrow().getGameTable();
        GameInfo gameInfo = gameMapper.toGameInfo(gameTable);
        MatchData matchData = matchDataBuilder.buildFromMatchTable(sessionList);

        List<ITeam> teamList = matchData.getTeamDataSet().stream().map(teamData -> {
            PlayerRankingMap playerRankingMap = new PlayerRankingMap();
            teamData.getPlayerTableSet().forEach(playerTable ->
                    playerRankingMap.put(playerTable, lastPlayerRating.get(playerTable.getName()).get(gameTable.getName())));
            return playerRankingMap;
        }).collect(Collectors.toList());
        int[] ranks = buildRanks(teamList, sessionList);
        Logger.debug("Old ratings for {}", gameTable.getName());
        for (int i = 0; i < teamList.size(); i++) {
            Logger.debug("(≻)={} T={}", ranks[i], teamList.get(i));
        }
        return skillCalculator.calculateNewRatings(gameInfo, teamList, Arrays.copyOf(ranks, ranks.length));
    }

    public Rating combine(final List<Rating> ratingList) {
        double mean = ratingList.stream().mapToDouble(Rating::getMean).average().orElseThrow();
        double stdev = ratingList.stream().mapToDouble(Rating::getStandardDeviation).average().orElseThrow();
        return new Rating(mean, stdev);
    }
}
