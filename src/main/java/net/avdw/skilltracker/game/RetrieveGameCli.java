package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@CommandLine.Command(name = "view", description = "View the details of a game", mixinStandardHelpOptions = true)
public class RetrieveGameCli implements Runnable {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Parameters(arity = "1")
    private String game;
    @Inject
    private GameService gameService;
    @Inject
    private MatchService matchService;
    @Inject
    @Game
    private ResourceBundle resourceBundle;
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        GameTable gameTable = gameService.retrieveGame(game);

        Map<String, List<MatchTable>> matchPlayerMap = matchService.retrieveAllMatchesForGame(gameTable);
        if (matchPlayerMap.isEmpty()) {
            spec.commandLine().getOut().println(resourceBundle.getString(GameBundleKey.NO_MATCH_FOUND));
        } else {
            matchPlayerMap.forEach((key, matchTableList) -> {
                Date date = matchTableList.stream().findAny().get().getPlayDate();
                String teams = matchTableList.stream().collect(Collectors.groupingBy(MatchTable::getTeam)).values().stream()
                        .map(teamList -> teamList.stream()
                                .map(matchTable -> String.format("[%s]%s(%s)", matchTable.getRank(), matchTable.getPlayerTable().getName(), matchTable.getMean().setScale(2, RoundingMode.HALF_UP)))
                                .collect(Collectors.joining(" & ")))
                        .collect(Collectors.joining(" vs. "));
                spec.commandLine().getOut().println(String.format("%s %s %s", SIMPLE_DATE_FORMAT.format(date), gameTable.getName(), teams));
            });
        }
    }
}
