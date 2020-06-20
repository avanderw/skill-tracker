package net.avdw.skilltracker.player;

import com.google.inject.Inject;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Command(name = "view", description = "View player information", mixinStandardHelpOptions = true)
public class RetrievePlayerCli implements Runnable {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    private MatchService matchService;
    @Parameters(arity = "1")
    private String name;
    @Inject
    private PlayerService playerService;
    @Inject
    @Player
    private ResourceBundle resourceBundle;
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        PlayerTable playerTable = playerService.retrievePlayer(name);
        if (playerTable == null) {
            spec.commandLine().getOut().println(resourceBundle.getString(PlayerBundleKey.PLAYER_NOT_EXIST));
            return;
        }

        List<MatchTable> matchTableList = matchService.retrieveAllMatchesForPlayer(playerTable);
        if (matchTableList.isEmpty()) {
            spec.commandLine().getOut().println(resourceBundle.getString(PlayerBundleKey.NO_MATCH_FOUND));
        } else {
            Date date = matchTableList.stream().findAny().get().getPlayDate();
            GameTable gameTable = matchTableList.stream().findAny().get().getGameTable();
            String teams = matchTableList.stream().collect(Collectors.groupingBy(MatchTable::getTeam)).values().stream()
                    .map(teamList -> teamList.stream()
                            .map(matchTable -> String.format("[%s]%s(%s)", matchTable.getRank(), matchTable.getPlayerTable().getName(), matchTable.getMean().setScale(2, RoundingMode.HALF_UP)))
                            .collect(Collectors.joining(" & ")))
                    .collect(Collectors.joining(" vs. "));
            spec.commandLine().getOut().println(String.format("%s %s %s", SIMPLE_DATE_FORMAT.format(date), gameTable.getName(), teams));
        }
    }
}
