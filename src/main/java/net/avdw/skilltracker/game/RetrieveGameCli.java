package net.avdw.skilltracker.game;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import net.avdw.skilltracker.player.PlayerService;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandLine.Command(name = "view", description = "View the details of a game", mixinStandardHelpOptions = true)
public class RetrieveGameCli implements Runnable {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @Parameters(arity = "1")
    private String game;
    @Inject
    private GameService gameService;
    private Gson gson = new Gson();
    private Long matchLimit = 5L;
    @Inject
    private MatchService matchService;
    private Long playerLimit = 10L;
    @Inject
    private PlayerService playerService;
    @Spec
    private CommandSpec spec;
    @Inject
    @Game
    private Templator templator;

    @Override
    public void run() {
        GameTable gameTable = gameService.retrieveGame(game);

        List<MatchTable> topPlayerList = playerService.retrieveTopPlayersForGame(gameTable, playerLimit);
        Map<String, List<MatchTable>> matchPlayerMap = matchService.retrieveAllMatchesForGame(gameTable);
        if (matchPlayerMap.isEmpty()) {
            templator.populate(GameBundleKey.NO_MATCH_FOUND);
        } else {
            spec.commandLine().getOut().println(templator.populate(GameBundleKey.VIEW_GAME_TOP_PLAYER_LIST_TITLE,
                    gson.fromJson(String.format("{limit:'%s'}", playerLimit), Map.class)));

            for (int i = 0; i < topPlayerList.size(); i++) {
                MatchTable table = topPlayerList.get(i);
                spec.commandLine().getOut().println(templator.populate(GameBundleKey.VIEW_GAME_TOP_PLAYER_LIST_ENTRY,
                        gson.fromJson(String.format("{name:'%s',mean:'%s',stdev:'%s',rank:'%s'}",
                                table.getPlayerTable().getName(),
                                table.getMean().setScale(0, RoundingMode.HALF_UP),
                                table.getStandardDeviation().setScale(0, RoundingMode.HALF_UP),
                                i + 1
                        ), Map.class)));
            }

            spec.commandLine().getOut().println(templator.populate(GameBundleKey.VIEW_GAME_MATCH_LIST_TITLE,
                    gson.fromJson(String.format("{limit:'%s'}", matchLimit), Map.class)));

            matchPlayerMap.forEach((key, matchTableList) -> {
                String teams = matchTableList.stream().collect(Collectors.groupingBy(MatchTable::getTeam)).values().stream()
                        .map(teamList -> {
                            String team = teamList.stream().map(matchTable -> templator.populate(GameBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                    gson.fromJson(String.format("{rank:'%s',name:'%s',mean:'%s'}",
                                            matchTable.getRank(),
                                            matchTable.getPlayerTable().getName(),
                                            matchTable.getMean().setScale(0, RoundingMode.HALF_UP)
                                    ), Map.class)))
                                    .collect(Collectors.joining(" & "));
                            team = templator.populate(GameBundleKey.MATCH_TEAM_ENTRY,
                                    gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                            teamList.stream().findAny().get().getRank(),
                                            team
                                    ), Map.class));
                            return team;
                        })
                        .collect(Collectors.joining(" vs. "));

                spec.commandLine().getOut().println(templator.populate(GameBundleKey.VIEW_GAME_MATCH_LIST_ENTRY,
                        gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s'}",
                                key.substring(0, key.indexOf("-")),
                                teams,
                                SIMPLE_DATE_FORMAT.format(matchTableList.stream().findAny().get().getPlayDate())
                        ), Map.class)));
            });
        }
    }
}
