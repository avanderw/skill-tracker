package net.avdw.skilltracker.player;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Command(name = "view", description = "View player information", mixinStandardHelpOptions = true)
public class RetrievePlayerCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Long gameLimit = 10L;
    @Inject
    private GameService gameService;
    @Inject
    private Gson gson;
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

    @Override
    public void run() {
        PlayerTable playerTable = playerService.retrievePlayer(name);
        if (playerTable == null) {
            spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_NOT_EXIST));
            return;
        }

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
        lastFewGameList.forEach(matchTable ->
                spec.commandLine().getOut().println(templator.populate(PlayerBundleKey.PLAYER_LAST_PLAYED_ENTRY,
                        gson.fromJson(String.format("{date:'%s',game:'%s',rank:'%s',session:'%s'}",
                                simpleDateFormat.format(matchTable.getPlayDate()),
                                matchTable.getGameTable().getName(),
                                matchTable.getRank(),
                                matchTable.getSessionId().substring(0, matchTable.getSessionId().indexOf("-"))), Map.class))));
    }
}
