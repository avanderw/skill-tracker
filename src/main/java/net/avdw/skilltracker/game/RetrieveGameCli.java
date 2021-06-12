package net.avdw.skilltracker.game;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.match.MatchService;
import picocli.CommandLine;
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

@CommandLine.Command(name = "view", description = "View the details of a game", mixinStandardHelpOptions = true)
public class RetrieveGameCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Parameters(arity = "1")
    private String game;
    @Inject
    private GameService gameService;
    private Gson gson = new Gson();
    @Option(names = "--last")
    private Long matchLimit = 5L;
    @Inject
    private MatchService matchService;
    @Option(names = "--top")
    private Long playerLimit = 10L;
    @Spec
    private CommandSpec spec;
    @Inject
    @Game
    private Templator templator;

    @Override
    public void run() {
        OrmLiteGame ormLiteGame = gameService.retrieveGame(game);

        if (ormLiteGame == null) {
            spec.commandLine().getOut().println(templator.populate(GameBundleKey.NO_GAME_FOUND));
            return;
        }

        List<PlayEntity> topPlayerMatchList = matchService.retrieveTopPlayerMatchesForGame(ormLiteGame, playerLimit);
        Map<String, List<PlayEntity>> matchPlayerMap = matchService.retrieveLastFewMatchesForGame(ormLiteGame, matchLimit)
                .stream().collect(Collectors.groupingBy(PlayEntity::getSessionId));
        if (matchPlayerMap.isEmpty()) {
            templator.populate(GameBundleKey.NO_MATCH_FOUND);
        } else {
            spec.commandLine().getOut().println(templator.populate(GameBundleKey.VIEW_GAME_TOP_PLAYER_LIST_TITLE,
                    gson.fromJson(String.format("{limit:'%s'}", playerLimit), Map.class)));

            for (int i = 0; i < topPlayerMatchList.size(); i++) {
                PlayEntity table = topPlayerMatchList.get(i);
                spec.commandLine().getOut().println(templator.populate(GameBundleKey.VIEW_GAME_TOP_PLAYER_LIST_ENTRY,
                        gson.fromJson(String.format("{name:'%s',mean:'%s',stdev:'%s',rank:'%s'}",
                                table.getPlayerName(),
                                table.getPlayerMean().setScale(0, RoundingMode.HALF_UP),
                                table.getPlayerStdDev().setScale(0, RoundingMode.HALF_UP),
                                String.format("%2s", i + 1)
                        ), Map.class)));
            }

            spec.commandLine().getOut().println(templator.populate(GameBundleKey.VIEW_GAME_MATCH_LIST_TITLE,
                    gson.fromJson(String.format("{limit:'%s'}", matchLimit), Map.class)));

            matchPlayerMap.entrySet().stream()
                    .sorted(Comparator.comparing((Map.Entry<String, List<PlayEntity>> entry) -> entry.getValue().get(0).getPlayDate()).reversed())
                    .forEach(entry -> {
                        String key = entry.getKey();
                        List<PlayEntity> ormLiteMatchList = entry.getValue();
                        String teams = ormLiteMatchList.stream().collect(Collectors.groupingBy(PlayEntity::getPlayerTeam)).values().stream()
                                .map(teamList -> {
                                    String team = teamList.stream().map(matchTable -> templator.populate(GameBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                            gson.fromJson(String.format("{rank:'%s',name:'%s',mean:'%s'}",
                                                    matchTable.getTeamRank(),
                                                    matchTable.getPlayerName(),
                                                    matchTable.getPlayerMean().setScale(0, RoundingMode.HALF_UP)
                                            ), Map.class)))
                                            .collect(Collectors.joining(" & "));
                                    team = templator.populate(GameBundleKey.MATCH_TEAM_ENTRY,
                                            gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                                    teamList.stream().findAny().get().getTeamRank() +1,
                                                    team
                                            ), Map.class));
                                    return team;
                                })
                                .collect(Collectors.joining(" vs. "));

                        spec.commandLine().getOut().println(templator.populate(GameBundleKey.VIEW_GAME_MATCH_LIST_ENTRY,
                                gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s'}",
                                        key.substring(0, key.indexOf("-")),
                                        teams,
                                        simpleDateFormat.format(ormLiteMatchList.stream().findAny().get().getPlayDate())
                                ), Map.class)));
                    });
        }
    }
}
