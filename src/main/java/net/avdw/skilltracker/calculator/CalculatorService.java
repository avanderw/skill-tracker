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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        int[] ranks = new int[teamList.size()];
        for (int i = 0; i < teamList.size(); i++) {
            ITeam team = teamList.get(i);
            String name = ((PlayerTable) team.keySet().stream().findAny().orElseThrow()).getName();
            ranks[i] = sessionMatchTableList.stream()
                    .filter(m -> m.getPlayerTable().getName().equals(name))
                    .findAny().orElseThrow().getRank();
        }
        return ranks;
    }

    public Map<IPlayer, Rating> calculate(final List<MatchTable> sessionList, final Map<String, Map<String, Rating>> lastPlayerRating) {
        Logger.trace("=> calculate");
        sessionList.forEach(session -> Logger.debug("{}=(μ)={} (σ)={}", session.getPlayerTable().getName(), session.getMean(), session.getStandardDeviation()));
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
        Logger.debug("calculating game={}", gameTable.getName());
        for (int i = 0; i < teamList.size(); i++) {
            Logger.debug("rank={} team={}", ranks[i], teamList.get(i));
        }
        return skillCalculator.calculateNewRatings(gameInfo, teamList, Arrays.copyOf(ranks, ranks.length));
    }
}
