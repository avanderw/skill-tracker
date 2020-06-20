package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import net.avdw.skilltracker.game.GameTable;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@CommandLine.Command(name = "ls", description = "List last few matches", mixinStandardHelpOptions = true)
public class ListMatchCli implements Runnable {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Long limit = 10L;
    @Inject
    private MatchService matchService;
    @Inject
    @Match
    private ResourceBundle resourceBundle;
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        List<MatchTable> matchTableList = matchService.retrieveLastFewMatches(limit);
        if (matchTableList.isEmpty()) {
            spec.commandLine().getOut().println(resourceBundle.getString(MatchBundleKey.NO_MATCH_FOUND));
            return;
        }

        matchTableList.stream().collect(Collectors.groupingBy(MatchTable::getSessionId)).forEach((key, matchTables) -> {
            Date date = matchTableList.stream().findAny().get().getPlayDate();
            GameTable gameTable = matchTableList.stream().findAny().get().getGameTable();
            String teams = matchTables.stream().collect(Collectors.groupingBy(MatchTable::getTeam)).values().stream()
                    .map(teamList -> teamList.stream()
                            .map(matchTable -> String.format("[%s]%s(%s)", matchTable.getRank(), matchTable.getPlayerTable().getName(), matchTable.getMean().setScale(2, RoundingMode.HALF_UP)))
                            .collect(Collectors.joining(" & ")))
                    .collect(Collectors.joining(" vs. "));
            spec.commandLine().getOut().println(String.format("%s %s %s", SIMPLE_DATE_FORMAT.format(date), gameTable.getName(), teams));
        });
    }
}
