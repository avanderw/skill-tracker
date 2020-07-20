package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.game.GameTable;
import org.tinylog.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Command(name = "add", description = "Add a match with an outcome", mixinStandardHelpOptions = true)
class CreateMatchCli implements Runnable {
    private final Gson gson = new Gson();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Option(names = {"-g", "--game"}, required = true)
    private String game;
    @Inject
    private PlayerRankingMapBuilder gameMatchTeamBuilder;
    @Inject
    private GameService gameService;
    @Inject
    private MatchDataBuilder matchDataBuilder;
    @Inject
    private MatchService matchService;
    @Option(names = {"-r", "--ranks"}, split = ",", required = true)
    private int[] ranks;
    @Spec
    private CommandSpec spec;
    @Parameters(description = "Teams in the match; team=<player1,player2> (no spaces)", arity = "1..*")
    private List<String> teams;
    @Inject
    @Match
    private Templator templator;

    @Override
    public void run() {
        if (teams == null) {
            throw new UnsupportedOperationException();
        }

        Logger.trace("Creating match");
        if (teams.size() == 1) {
            teams = Arrays.asList(teams.get(0).split(","));
        }
        if (ranks.length != teams.size()) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.TEAM_RANK_COUNT_MISMATCH));
        }

        GameTable gameTable = gameService.retrieveGame(game);
        if (gameTable == null) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.NO_GAME_FOUND));
            return;
        }
        MatchData matchData = matchDataBuilder.buildFromString(teams);
        List<ITeam> teamList = gameMatchTeamBuilder.build(gameTable, matchData);
        List<MatchTable> matchTableList = matchService.createMatchForGame(gameTable, teamList, ranks);

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.CREATE_SUCCESS));
        matchTableList.stream().collect(Collectors.groupingBy(MatchTable::getSessionId)).forEach((key, matchTables) -> {
            String teams = matchTables.stream().collect(Collectors.groupingBy(MatchTable::getTeam)).values().stream()
                    .map(t -> {
                        String team = t.stream().map(matchTable -> templator.populate(MatchBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                gson.fromJson(String.format("{name:'%s'}",
                                        matchTable.getPlayerTable().getName()), Map.class)))
                                .collect(Collectors.joining(" & "));
                        team = templator.populate(MatchBundleKey.MATCH_TEAM_ENTRY,
                                gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                        t.stream().findAny().orElseThrow().getRank(),
                                        team), Map.class));
                        return team;
                    })
                    .collect(Collectors.joining(" vs. "));

            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.LAST_MATCH_LIST_ENTRY,
                    gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s',game:'%s'}",
                            key.substring(0, key.indexOf("-")),
                            teams,
                            simpleDateFormat.format(matchTableList.stream().findAny().orElseThrow().getPlayDate()),
                            matchTableList.stream().findAny().orElseThrow().getGameTable().getName()), Map.class)));
        });

        CommandLine commandLine = spec.parent().parent().commandLine();
        PrintWriter original = commandLine.getOut();
        StringWriter out = new StringWriter();
        commandLine.setOut(new PrintWriter(out));
        commandLine.execute("game", "view", game);
        commandLine.setOut(original);
        spec.commandLine().getOut().println();
        spec.commandLine().getOut().println(out.toString());
    }
}
