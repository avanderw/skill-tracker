package net.avdw.skilltracker.adapter.in.cli.player;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.PlayerDbAdapter;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.player.PlayerBundleKey;
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
public class ViewPlayerCli implements Runnable {
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
    private PlayerDbAdapter playerDbAdapter;
    @Spec
    private CommandSpec spec;
    @Inject
    private Templator templator;

    private void printAllGameInfo(final OrmLitePlayer ormLitePlayer) {
        spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_TOP_GAME_LIST_TITLE,
                gson.fromJson(String.format("{limit:'%s'}", gameLimit), Map.class)));
        List<PlayEntity> topGameList = gameService.retrieveTopGamesForPlayer(ormLitePlayer, gameLimit);
        for (int i = 0; i < topGameList.size(); i++) {
            PlayEntity table = topGameList.get(i);
            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_TOP_GAME_LIST_ENTRY,
                    gson.fromJson(String.format("{name:'%s',mean:'%s',stdev:'%s',rank:'%s'}",
                            table.getGameName(),
                            table.getPlayerMean().setScale(0, RoundingMode.HALF_UP),
                            table.getPlayerStdDev().setScale(0, RoundingMode.HALF_UP),
                            i + 1), Map.class)));
        }

        spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_LAST_PLAYED_TITLE,
                gson.fromJson(String.format("{limit:'%s'}", matchLimit), Map.class)));
        List<PlayEntity> lastFewGameList = matchService.retrieveLastFewMatchesForPlayer(ormLitePlayer, matchLimit);
        Map<String, List<PlayEntity>> sessionMatchTableMap = lastFewGameList.stream().collect(Collectors.groupingBy(PlayEntity::getSessionId));
        List<Map.Entry<String, List<PlayEntity>>> sortedEntryList = sessionMatchTableMap.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, List<PlayEntity>> entry) -> entry.getValue().get(0).getPlayDate()).reversed())
                .collect(Collectors.toList());

        sortedEntryList.forEach(entry -> {
            String teams = entry.getValue().stream().collect(Collectors.groupingBy(PlayEntity::getPlayerTeam)).values().stream()
                    .sorted(Comparator.comparingInt(list -> list.get(0).getTeamRank()))
                    .map(teamList -> {
                        String team = teamList.stream().map(matchTable -> templator.populate(PlayerBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                gson.fromJson(String.format("{name:'%s'}",
                                        matchTable.getPlayerName()), Map.class)))
                                .collect(Collectors.joining(" & "));
                        team = templator.populate(PlayerBundleKey.MATCH_TEAM_ENTRY,
                                gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                        teamList.stream().findAny().orElseThrow().getTeamRank(),
                                        team), Map.class));
                        return team;
                    })
                    .collect(Collectors.joining(" vs. "));

            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_LAST_PLAYED_ENTRY,
                    gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s',game:'%s',mean:'%.0f',stdev:'%.0f'}",
                            entry.getKey().substring(0, entry.getKey().indexOf("-")),
                            teams,
                            simpleDateFormat.format(entry.getValue().stream().findAny().orElseThrow().getPlayDate()),
                            entry.getValue().stream().findAny().orElseThrow().getGameName(),
                            entry.getValue().stream().filter(m -> m.getPlayerName().equals(name)).mapToDouble(m -> m.getPlayerMean().doubleValue()).average().orElseThrow(),
                            entry.getValue().stream().filter(m -> m.getPlayerName().equals(name)).mapToDouble(m -> m.getPlayerStdDev().doubleValue()).average().orElseThrow()),
                            Map.class)));
        });
    }

    private void printSpecificGameInfo(final OrmLitePlayer ormLitePlayer) {
        OrmLiteGame ormLiteGame = gameService.retrieveGame(game);
        List<PlayEntity> playerGameMatchList = matchService.retrieveLastFewMatchesForGameAndPlayer(ormLiteGame, ormLitePlayer, matchLimit);
        Map<String, List<PlayEntity>> sessionMatchTableMap = playerGameMatchList.stream().collect(Collectors.groupingBy(PlayEntity::getSessionId));
        List<Map.Entry<String, List<PlayEntity>>> sortedEntryList = sessionMatchTableMap.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, List<PlayEntity>> entry) -> entry.getValue().get(0).getPlayDate()).reversed())
                .collect(Collectors.toList());

        PlayEntity ormLiteMatch = matchService.retrieveLastPlayerMatchForGame(ormLiteGame, ormLitePlayer);
        spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.SPECIFIC_GAME_TITLE,
                gson.fromJson(String.format("{game:'%s',person:'%s',mean:'%s',stdev:'%s',played:'%s'}",
                        ormLiteGame.getName(),
                        ormLitePlayer.getName(),
                        ormLiteMatch.getPlayerMean().setScale(0, RoundingMode.HALF_UP),
                        ormLiteMatch.getPlayerStdDev().setScale(0, RoundingMode.HALF_UP),
                        matchService.retrieveAllMatchesForGameAndPlayer(ormLiteGame, ormLitePlayer).size()), Map.class)));

        spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_LAST_PLAYED_TITLE,
                gson.fromJson(String.format("{limit:'%s'}", matchLimit), Map.class)));
        sortedEntryList.forEach(entry -> {
            String teams = entry.getValue().stream().collect(Collectors.groupingBy(PlayEntity::getPlayerTeam)).values().stream()
                    .sorted(Comparator.comparingInt(list -> list.get(0).getTeamRank()))
                    .map(teamList -> {
                        String team = teamList.stream()
                                .map(m -> templator.populate(PlayerBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                        gson.fromJson(String.format("{name:'%s'}",
                                                m.getPlayerName()), Map.class)))
                                .collect(Collectors.joining(" & "));
                        team = templator.populate(PlayerBundleKey.MATCH_TEAM_ENTRY,
                                gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                        teamList.stream().findAny().orElseThrow().getTeamRank(),
                                        team), Map.class));
                        return team;
                    })
                    .collect(Collectors.joining(" vs. "));

            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.SPECIFIC_GAME_LAST_PLAYED_ENTRY,
                    gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s',mean:'%.0f',stdev:'%.0f'}",
                            entry.getKey().substring(0, entry.getKey().indexOf("-")),
                            teams,
                            simpleDateFormat.format(entry.getValue().stream().findAny().orElseThrow().getPlayDate()),
                            entry.getValue().stream().filter(m -> m.getPlayerName().equals(name)).mapToDouble(m -> m.getPlayerMean().doubleValue()).average().orElseThrow(),
                            entry.getValue().stream().filter(m -> m.getPlayerName().equals(name)).mapToDouble(m -> m.getPlayerStdDev().doubleValue()).average().orElseThrow()),
                            Map.class)));
        });
    }

    @Override
    public void run() {
        OrmLitePlayer ormLitePlayer = playerDbAdapter.retrievePlayer(name);
        if (ormLitePlayer == null) {
            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_NOT_EXIST));
            return;
        }

        if (game == null) {
            printAllGameInfo(ormLitePlayer);
        } else {
            printSpecificGameInfo(ormLitePlayer);
        }
    }
}
