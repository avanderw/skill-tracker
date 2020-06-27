package net.avdw.skilltracker.player;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Command(name = "view", description = "View player information", mixinStandardHelpOptions = true)
public class RetrievePlayerCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Option(names = {"-g", "--game"})
    private String game;
    @Option(names = "--top")
    private Long gameLimit = 5L;
    @Inject
    private GameService gameService;
    @Inject
    private Gson gson;
    @Option(names = "--last")
    private Long matchLimit = 5L;
    @Inject
    private MatchService matchService;
    @Parameters(arity = "1")
    private String name;
    @Inject
    private PlayerService playerService;
    @Spec
    private CommandSpec spec;
    @Inject
    @Player
    private Templator templator;

    private void printAllGameInfo(final PlayerTable playerTable) {
        spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_TOP_GAME_LIST_TITLE,
                gson.fromJson(String.format("{limit:'%s'}", gameLimit), Map.class)));
        List<MatchTable> topGameList = gameService.retrieveTopGamesForPlayer(playerTable, gameLimit);
        for (int i = 0; i < topGameList.size(); i++) {
            MatchTable table = topGameList.get(i);
            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_TOP_GAME_LIST_ENTRY,
                    gson.fromJson(String.format("{name:'%s',mean:'%s',stdev:'%s',rank:'%s'}",
                            table.getGameTable().getName(),
                            table.getMean().setScale(0, RoundingMode.HALF_UP),
                            table.getStandardDeviation().setScale(0, RoundingMode.HALF_UP),
                            i + 1), Map.class)));
        }

        spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_LAST_PLAYED_TITLE,
                gson.fromJson(String.format("{limit:'%s'}", matchLimit), Map.class)));
        List<MatchTable> lastFewGameList = matchService.retrieveLastFewMatchesForPlayer(playerTable, matchLimit);
        Map<String, List<MatchTable>> sessionMatchTableMap = lastFewGameList.stream().collect(Collectors.groupingBy(MatchTable::getSessionId));
        List<Map.Entry<String, List<MatchTable>>> sortedEntryList = sessionMatchTableMap.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, List<MatchTable>> entry) -> entry.getValue().get(0).getPlayDate()).reversed())
                .collect(Collectors.toList());

        sortedEntryList.forEach(entry -> {
            String teams = entry.getValue().stream().collect(Collectors.groupingBy(MatchTable::getTeam)).values().stream()
                    .sorted(Comparator.comparingInt(list -> list.get(0).getRank()))
                    .map(teamList -> {
                        String team = teamList.stream().map(matchTable -> templator.populate(PlayerBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                gson.fromJson(String.format("{rank:'%s',name:'%s',mean:'%s'}",
                                        matchTable.getRank(),
                                        matchTable.getPlayerTable().getName(),
                                        matchTable.getMean().setScale(0, RoundingMode.HALF_UP)), Map.class)))
                                .collect(Collectors.joining(" & "));
                        team = templator.populate(PlayerBundleKey.MATCH_TEAM_ENTRY,
                                gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                        teamList.stream().findAny().get().getRank(),
                                        team), Map.class));
                        return team;
                    })
                    .collect(Collectors.joining(" vs. "));

            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_LAST_PLAYED_ENTRY,
                    gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s',game:'%s',mean:'%s',stdev:'%s'}",
                            entry.getKey().substring(0, entry.getKey().indexOf("-")),
                            teams,
                            simpleDateFormat.format(entry.getValue().stream().findAny().get().getPlayDate()),
                            entry.getValue().stream().findAny().get().getGameTable().getName(),
                            entry.getValue().stream().filter(m -> m.getPlayerTable().getName().equals(name)).findAny().get().getMean().setScale(0, RoundingMode.HALF_UP),
                            entry.getValue().stream().filter(m -> m.getPlayerTable().getName().equals(name)).findAny().get().getStandardDeviation().setScale(0, RoundingMode.HALF_UP)),
                            Map.class)));
        });
    }

    private void printSpecificGameInfo(final PlayerTable playerTable) {
        GameTable gameTable = gameService.retrieveGame(game);
        List<MatchTable> playerGameMatchList = matchService.retrieveLastFewMatchesForGameAndPlayer(gameTable, playerTable, matchLimit);
        Map<String, List<MatchTable>> sessionMatchTableMap = playerGameMatchList.stream().collect(Collectors.groupingBy(MatchTable::getSessionId));
        List<Map.Entry<String, List<MatchTable>>> sortedEntryList = sessionMatchTableMap.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, List<MatchTable>> entry) -> entry.getValue().get(0).getPlayDate()).reversed())
                .collect(Collectors.toList());

        MatchTable matchTable = matchService.retrieveLatestPlayerMatchForGame(gameTable, playerTable);
        spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.SPECIFIC_GAME_TITLE,
                gson.fromJson(String.format("{game:'%s',person:'%s',mean:'%s',stdev:'%s',played:'%s'}",
                        gameTable.getName(),
                        playerTable.getName(),
                        matchTable.getMean().setScale(0, RoundingMode.HALF_UP),
                        matchTable.getStandardDeviation().setScale(0, RoundingMode.HALF_UP),
                        matchService.retrieveAllMatchesForGameAndPlayer(gameTable, playerTable).size()), Map.class)));

        spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_LAST_PLAYED_TITLE,
                gson.fromJson(String.format("{limit:'%s'}", matchLimit), Map.class)));
        sortedEntryList.forEach(entry -> {
            String teams = entry.getValue().stream().collect(Collectors.groupingBy(MatchTable::getTeam)).values().stream()
                    .sorted(Comparator.comparingInt(list -> list.get(0).getRank()))
                    .map(teamList -> {
                        String team = teamList.stream()
                                .map(m -> templator.populate(PlayerBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                        gson.fromJson(String.format("{rank:'%s',name:'%s',mean:'%s'}",
                                                m.getRank(),
                                                m.getPlayerTable().getName(),
                                                m.getMean().setScale(0, RoundingMode.HALF_UP)), Map.class)))
                                .collect(Collectors.joining(" & "));
                        team = templator.populate(PlayerBundleKey.MATCH_TEAM_ENTRY,
                                gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                        teamList.stream().findAny().get().getRank(),
                                        team), Map.class));
                        return team;
                    })
                    .collect(Collectors.joining(" vs. "));

            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.SPECIFIC_GAME_LAST_PLAYED_ENTRY,
                    gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s',mean:'%s',stdev:'%s'}",
                            entry.getKey().substring(0, entry.getKey().indexOf("-")),
                            teams,
                            simpleDateFormat.format(entry.getValue().stream().findAny().get().getPlayDate()),
                            entry.getValue().stream().filter(m -> m.getPlayerTable().getName().equals(playerTable.getName())).findAny().get().getMean().setScale(0, RoundingMode.HALF_UP),
                            entry.getValue().stream().filter(m -> m.getPlayerTable().getName().equals(playerTable.getName())).findAny().get().getStandardDeviation().setScale(0, RoundingMode.HALF_UP)),
                            Map.class)));
        });
    }

    @Override
    public void run() {
        PlayerTable playerTable = playerService.retrievePlayer(name);
        if (playerTable == null) {
            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_NOT_EXIST));
            return;
        }

        if (game == null) {
            printAllGameInfo(playerTable);
        } else {
            printSpecificGameInfo(playerTable);
        }
    }
}
