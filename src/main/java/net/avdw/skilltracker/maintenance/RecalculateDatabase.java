package net.avdw.skilltracker.maintenance;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.gesundkrank.jskills.*;
import net.avdw.skilltracker.MainModule;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.game.GameMapper;
import net.avdw.skilltracker.match.MatchData;
import net.avdw.skilltracker.match.MatchDataBuilder;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.PlayerRankingMap;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public final class RecalculateDatabase implements Runnable {
    private final GameMapper gameMapper;
    private final MatchDataBuilder matchDataBuilder;
    private final MatchService matchService;
    private final SkillCalculator skillCalculator;

    @Inject
    RecalculateDatabase(final MatchService matchService,
                        final SkillCalculator skillCalculator,
                        final GameMapper gameMapper,
                        final MatchDataBuilder matchDataBuilder) {
        this.matchService = matchService;
        this.skillCalculator = skillCalculator;
        this.gameMapper = gameMapper;
        this.matchDataBuilder = matchDataBuilder;
    }

    public static void main(final String[] args) {
        Guice.createInjector(new MainModule()).getInstance(RecalculateDatabase.class).run();
    }

    private int[] buildRanks(final List<ITeam> teamList, final List<PlayEntity> sessionOrmLiteMatchList) {
        int[] ranks = new int[teamList.size()];
        for (int i = 0; i < teamList.size(); i++) {
            ITeam team = teamList.get(i);
            String name = ((OrmLitePlayer) team.keySet().stream().findAny().orElseThrow()).getName();
            ranks[i] = sessionOrmLiteMatchList.stream()
                    .filter(m -> m.getPlayerName().equals(name))
                    .findAny().orElseThrow().getTeamRank();
        }
        return ranks;
    }

    @Override
    public void run() {
        List<String> sessionIdList = new ArrayList<>();
        List<PlayEntity> ormLiteMatchList = matchService.retrieveAllMatches();
        Map<String, List<PlayEntity>> groupBySessionMap = ormLiteMatchList.stream().collect(Collectors.groupingBy(PlayEntity::getSessionId));
        Map<String, Map<String, Rating>> lastPlayerRating = new HashMap<>();
        groupBySessionMap.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().get(0).getPlayDate())).forEach(e -> {
            String sessionId = e.getKey();
            sessionIdList.add(sessionId);

            List<PlayEntity> sessionOrmLiteMatchList = e.getValue();
            OrmLiteGame ormLiteGame = new OrmLiteGame(sessionOrmLiteMatchList.stream().findAny().orElseThrow().getGameName());
            GameInfo gameInfo = gameMapper.toGameInfo(ormLiteGame);

            MatchData matchData = matchDataBuilder.buildFromMatchTable(sessionOrmLiteMatchList);
            matchData.getTeamDataSet().forEach(team -> {
                team.getOrmLitePlayerSet().forEach(player -> {
                    lastPlayerRating.putIfAbsent(player.getName(), new HashMap<>());
                    lastPlayerRating.get(player.getName()).putIfAbsent(ormLiteGame.getName(), gameInfo.getDefaultRating());
                });
            });

            List<ITeam> teamList = matchData.getTeamDataSet().stream().map(teamData -> {
                PlayerRankingMap playerRankingMap = new PlayerRankingMap();
                teamData.getOrmLitePlayerSet().forEach(playerTable ->
                        playerRankingMap.put(playerTable, lastPlayerRating.get(playerTable.getName()).get(ormLiteGame.getName())));
                return playerRankingMap;
            }).collect(Collectors.toList());
            int[] ranks = buildRanks(teamList, sessionOrmLiteMatchList);
            Map<IPlayer, Rating> newRanking = skillCalculator.calculateNewRatings(gameInfo, teamList, Arrays.copyOf(ranks, ranks.length));

            Logger.debug("Processed: {} sessions", sessionIdList.size());
            newRanking.forEach((key, rating) -> {
                OrmLitePlayer player = (OrmLitePlayer) key;
                Logger.trace("New ranking for {} {}", player, rating);
                lastPlayerRating.get(player.getName()).put(ormLiteGame.getName(), rating);
                BigDecimal recalcMean = BigDecimal.valueOf(rating.getMean()).setScale(5, RoundingMode.HALF_UP);
                BigDecimal recalcStdev = BigDecimal.valueOf(rating.getStandardDeviation()).setScale(5, RoundingMode.HALF_UP);

                List<PlayEntity> matchingOrmLiteMatches = sessionOrmLiteMatchList.stream()
                        .filter(m -> m.getPlayerName().equals(player.getName()))
                        .collect(Collectors.toList());

                if (matchingOrmLiteMatches.isEmpty()) {
                    throw new UnsupportedOperationException();
                } else if (matchingOrmLiteMatches.size() == 1) {
                    PlayEntity ormLiteMatch = matchingOrmLiteMatches.get(0);
                    BigDecimal mean = ormLiteMatch.getPlayerMean().setScale(5, RoundingMode.HALF_UP);
                    BigDecimal stdev = ormLiteMatch.getPlayerStdDev().setScale(5, RoundingMode.HALF_UP);
                    if (!mean.equals(recalcMean) || !stdev.equals(recalcStdev)) {
                        sessionOrmLiteMatchList.forEach(m -> Logger.debug("> %s", m));
                        for (int i = 0; i < teamList.size(); i++) {
                            Logger.debug("> rank={}, team={}", ranks[i], teamList.get(i));
                        }
                        Logger.warn("player={}, rank={}, mean={} != {}, stdev={} != {}",
                                ormLiteMatch.getPlayerName(), ormLiteMatch.getTeamRank(), mean, recalcMean, stdev, recalcStdev);
                    } else {
                        Logger.trace("{} checks out", player);
                    }
                } else {
                    Logger.debug("Found duplicate player {} ({})", player.getName(), sessionId);
                    AtomicBoolean foundMatch = new AtomicBoolean(false);
                    matchingOrmLiteMatches.forEach(matchTable -> {
                        BigDecimal mean = matchTable.getPlayerMean().setScale(5, RoundingMode.HALF_UP);
                        BigDecimal stdev = matchTable.getPlayerStdDev().setScale(5, RoundingMode.HALF_UP);
                        sessionOrmLiteMatchList.forEach(m -> Logger.trace("> %s", m));
                        for (int i = 0; i < teamList.size(); i++) {
                            Logger.trace("> rank={}, team={}", ranks[i], teamList.get(i));
                        }
                        Logger.trace("player={}, rank={}, mean={} != {}, stdev={} != {}",
                                matchTable.getPlayerName(), matchTable.getTeamRank(), mean, recalcMean, stdev, recalcStdev);

                        if (!mean.equals(recalcMean) || !stdev.equals(recalcStdev)) {
                            throw new UnsupportedOperationException();
                        } else {
                            foundMatch.set(true);
                        }
                    });
                    if (foundMatch.get()) {
                        Logger.debug("Duplicate: {} checks out", player);
                        if (sessionIdList.size() != 13 && sessionIdList.size() != 17 && sessionIdList.size() != 18 && sessionIdList.size() != 34) {
                            throw new UnsupportedOperationException();
                        }
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
            });
        });

    }
}
