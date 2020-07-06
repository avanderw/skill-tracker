package net.avdw.skilltracker.maintenance;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.gesundkrank.jskills.*;
import net.avdw.skilltracker.MainModule;
import net.avdw.skilltracker.game.GameMapper;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.match.*;
import net.avdw.skilltracker.player.PlayerTable;
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

    @Override
    public void run() {
        List<String> sessionIdList = new ArrayList<>();
        List<MatchTable> matchTableList = matchService.retrieveAllMatches();
        Map<String, List<MatchTable>> groupBySessionMap = matchTableList.stream().collect(Collectors.groupingBy(MatchTable::getSessionId));
        Map<String, Map<String, Rating>> lastPlayerRating = new HashMap<>();
        groupBySessionMap.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().get(0).getPlayDate())).forEach(e -> {
            String sessionId = e.getKey();
            sessionIdList.add(sessionId);

            List<MatchTable> sessionMatchTableList = e.getValue();
            GameTable gameTable = sessionMatchTableList.stream().findAny().orElseThrow().getGameTable();
            GameInfo gameInfo = gameMapper.toGameInfo(gameTable);

            MatchData matchData = matchDataBuilder.buildFromMatchTable(sessionMatchTableList);
            matchData.getTeamDataSet().forEach(team -> {
                team.getPlayerTableSet().forEach(player -> {
                    lastPlayerRating.putIfAbsent(player.getName(), new HashMap<>());
                    lastPlayerRating.get(player.getName()).putIfAbsent(gameTable.getName(), gameInfo.getDefaultRating());
                });
            });

            List<ITeam> teamList = matchData.getTeamDataSet().stream().map(teamData -> {
                PlayerRankingMap playerRankingMap = new PlayerRankingMap();
                teamData.getPlayerTableSet().forEach(playerTable ->
                        playerRankingMap.put(playerTable, lastPlayerRating.get(playerTable.getName()).get(gameTable.getName())));
                return playerRankingMap;
            }).collect(Collectors.toList());
            int[] ranks = buildRanks(teamList, sessionMatchTableList);
            Map<IPlayer, Rating> newRanking = skillCalculator.calculateNewRatings(gameInfo, teamList, Arrays.copyOf(ranks, ranks.length));

            Logger.debug("Processed: {} sessions", sessionIdList.size());
            newRanking.forEach((key, rating) -> {
                PlayerTable player = (PlayerTable) key;
                Logger.trace("New ranking for {} {}", player, rating);
                lastPlayerRating.get(player.getName()).put(gameTable.getName(), rating);
                BigDecimal recalcMean = BigDecimal.valueOf(rating.getMean()).setScale(5, RoundingMode.HALF_UP);
                BigDecimal recalcStdev = BigDecimal.valueOf(rating.getStandardDeviation()).setScale(5, RoundingMode.HALF_UP);

                List<MatchTable> matchingMatchTables = sessionMatchTableList.stream()
                        .filter(m -> m.getPlayerTable().getName().equals(player.getName()))
                        .collect(Collectors.toList());

                if (matchingMatchTables.isEmpty()) {
                    throw new UnsupportedOperationException();
                } else if (matchingMatchTables.size() == 1) {
                    MatchTable matchTable = matchingMatchTables.get(0);
                    BigDecimal mean = matchTable.getMean().setScale(5, RoundingMode.HALF_UP);
                    BigDecimal stdev = matchTable.getStandardDeviation().setScale(5, RoundingMode.HALF_UP);
                    if (!mean.equals(recalcMean) || !stdev.equals(recalcStdev)) {
                        sessionMatchTableList.forEach(m -> Logger.debug("> session={}, pk={}, fk={}, player={}, team={}, rank={}, game={}, mean={}, stdev={}",
                                m.getSessionId(), m.getPk(), m.getPlayerTable().getPk(), m.getPlayerTable().getName(), m.getTeam(), m.getRank(), m.getGameTable().getName(), m.getMean(), m.getStandardDeviation()));
                        for (int i = 0; i < teamList.size(); i++) {
                            Logger.debug("> rank={}, team={}", ranks[i], teamList.get(i));
                        }
                        Logger.warn("player={}, rank={}, mean={} != {}, stdev={} != {}",
                                matchTable.getPlayerTable().getName(), matchTable.getRank(), mean, recalcMean, stdev, recalcStdev);
                    } else {
                        Logger.trace("{} checks out", player);
                    }
                } else {
                    Logger.debug("Found duplicate player {} ({})", player.getName(), sessionId);
                    AtomicBoolean foundMatch = new AtomicBoolean(false);
                    matchingMatchTables.forEach(matchTable -> {
                        BigDecimal mean = matchTable.getMean().setScale(5, RoundingMode.HALF_UP);
                        BigDecimal stdev = matchTable.getStandardDeviation().setScale(5, RoundingMode.HALF_UP);
                        sessionMatchTableList.forEach(m -> Logger.trace("> session={}, pk={}, fk={}, player={}, team={}, rank={}, game={}, mean={}, stdev={}",
                                m.getSessionId(), m.getPk(), m.getPlayerTable().getPk(), m.getPlayerTable().getName(), m.getTeam(), m.getRank(), m.getGameTable().getName(), m.getMean(), m.getStandardDeviation()));
                        for (int i = 0; i < teamList.size(); i++) {
                            Logger.trace("> rank={}, team={}", ranks[i], teamList.get(i));
                        }
                        Logger.trace("player={}, rank={}, mean={} != {}, stdev={} != {}",
                                matchTable.getPlayerTable().getName(), matchTable.getRank(), mean, recalcMean, stdev, recalcStdev);

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
