package net.avdw.skilltracker.calculator;

import com.google.inject.Inject;
import de.gesundkrank.jskills.*;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.game.GameMapper;
import net.avdw.skilltracker.match.MatchData;
import net.avdw.skilltracker.match.MatchDataBuilder;
import net.avdw.skilltracker.match.PlayerRankingMap;
import org.tinylog.Logger;

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

    private int[] buildRanks(final List<ITeam> teamList, final List<PlayEntity> sessionOrmLiteMatchList) {
        Logger.debug("-> build ranks");
        sessionOrmLiteMatchList.forEach(m -> Logger.debug("(≻)={} (μ)={} (σ)={} {}", m.getTeamRank(), m.getPlayerMean(), m.getPlayerStdDev(), m.getPlayerName()));
        int[] ranks = new int[teamList.size()];
        List<PlayEntity> ormLiteMatchList = new ArrayList<>(sessionOrmLiteMatchList);
        ormLiteMatchList.sort(Comparator.comparingInt(PlayEntity::getTeamRank));
        for (int i = teamList.size(); i > 0; i--) {
            ITeam team = teamList.get(i - 1);
            String name = ((OrmLitePlayer) team.keySet().stream().findAny().orElseThrow()).getName();

            for (int j = ormLiteMatchList.size(); j > 0; j--) {
                PlayEntity m = ormLiteMatchList.get(j - 1);
                if (m.getPlayerName().equals(name)) {
                    ranks[i - 1] = m.getTeamRank();
                    ormLiteMatchList.remove(m);
                    ormLiteMatchList.forEach(Logger::debug);
                    break;
                }
            }
        }
        Logger.debug("<- build ranks {}", Arrays.toString(ranks));
        return ranks;
    }

    public Map<IPlayer, Rating> calculate(final List<PlayEntity> sessionList, final Map<String, Map<String, Rating>> lastPlayerRating) {
        Logger.trace("=> calculate");
        sessionList.forEach(session -> Logger.debug("(μ)={} (σ)={} (≻)={} {}", session.getPlayerMean(), session.getPlayerStdDev(), session.getTeamRank(), session.getPlayerName()));
        lastPlayerRating.entrySet().forEach(Logger::debug);
        OrmLiteGame ormLiteGame = new OrmLiteGame(sessionList.stream().findAny().orElseThrow().getGameName());
        GameInfo gameInfo = gameMapper.toGameInfo(ormLiteGame);
        MatchData matchData = matchDataBuilder.buildFromMatchTable(sessionList);

        List<ITeam> teamList = matchData.getTeamDataSet().stream().map(teamData -> {
            PlayerRankingMap playerRankingMap = new PlayerRankingMap();
            teamData.getOrmLitePlayerSet().forEach(playerTable ->
                    playerRankingMap.put(playerTable, lastPlayerRating.get(playerTable.getName()).get(ormLiteGame.getName())));
            return playerRankingMap;
        }).collect(Collectors.toList());
        int[] ranks = buildRanks(teamList, sessionList);
        Logger.debug("Old ratings for {}", ormLiteGame.getName());
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
