package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandLine.Command(name = "ls", description = "List last few matches", mixinStandardHelpOptions = true)
public class ListMatchCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Gson gson = new Gson();
    @Option(names = {"-l", "--limit"}, description = "Limit the result list")
    private Long limit = 10L;
    @Inject
    private MatchService matchService;
    @Spec
    private CommandSpec spec;
    @Inject
    @Match
    private Templator templator;

    @Override
    public void run() {
        List<MatchTable> matchTableList = matchService.retrieveLastFewMatches(limit);
        if (matchTableList.isEmpty()) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.NO_MATCH_FOUND));
            return;
        }

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.LAST_MATCH_LIST_TITLE,
                gson.fromJson(String.format("{limit:'%s'}", limit), Map.class)));

        matchTableList.stream().collect(Collectors.groupingBy(MatchTable::getSessionId)).forEach((key, matchTables) -> {
            String teams = matchTables.stream().collect(Collectors.groupingBy(MatchTable::getTeam)).values().stream()
                    .map(teamList -> {
                        String team = teamList.stream().map(matchTable -> templator.populate(MatchBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                    gson.fromJson(String.format("{rank:'%s',name:'%s',mean:'%s'}",
                                            matchTable.getRank(),
                                            matchTable.getPlayerTable().getName(),
                                            matchTable.getMean().setScale(0, RoundingMode.HALF_UP)), Map.class)))
                                    .collect(Collectors.joining(" & "));
                        team = templator.populate(MatchBundleKey.MATCH_TEAM_ENTRY,
                                gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                        teamList.stream().findAny().get().getRank(),
                                        team), Map.class));
                        return team;
                    })
                    .collect(Collectors.joining(" vs. "));

            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.LAST_MATCH_LIST_ENTRY,
                    gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s',game:'%s'}",
                            key.substring(0, key.indexOf("-")),
                            teams,
                            simpleDateFormat.format(matchTableList.stream().findAny().get().getPlayDate()),
                            matchTableList.stream().findAny().get().getGameTable().getName()), Map.class)));
        });
    }
}
