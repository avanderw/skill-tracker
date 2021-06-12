package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandLine.Command(name = "ls", description = "List last few matches", mixinStandardHelpOptions = true)
public class ListMatchCli implements Runnable {
    private final Gson gson = new Gson();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Option(names = {"-l", "--limit"}, description = "Limit the result list")
    private Long limit = 10L;
    @Inject
    private MatchService matchService;
    @Spec
    private CommandSpec spec;
    @Inject
    @MatchScope
    private Templator templator;

    @Override
    public void run() {
        List<PlayEntity> ormLiteMatchList = matchService.retrieveLastFewMatches(limit);
        if (ormLiteMatchList.isEmpty()) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.NO_MATCH_FOUND));
            return;
        }

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.LAST_MATCH_LIST_TITLE,
                gson.fromJson(String.format("{limit:'%s'}", limit), Map.class)));

        Map<String, List<PlayEntity>> sessionMatchTableMap = ormLiteMatchList.stream().collect(Collectors.groupingBy(PlayEntity::getSessionId));
        List<Map.Entry<String, List<PlayEntity>>> sortedEntryList = sessionMatchTableMap.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, List<PlayEntity>> entry) -> entry.getValue().get(0).getPlayDate()).reversed())
                .collect(Collectors.toList());
        sortedEntryList.forEach(entry -> {
            String teams = entry.getValue().stream().collect(Collectors.groupingBy(PlayEntity::getPlayerTeam)).values().stream()
                    .map(teamList -> {
                        String team = teamList.stream().map(matchTable -> templator.populate(MatchBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                gson.fromJson(String.format("{name:'%s'}",
                                        matchTable.getPlayerName()), Map.class)))
                                .collect(Collectors.joining(" & "));
                        team = templator.populate(MatchBundleKey.MATCH_TEAM_ENTRY,
                                gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                        teamList.stream().findAny().get().getTeamRank(),
                                        team), Map.class));
                        return team;
                    })
                    .collect(Collectors.joining(" vs. "));

            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.LAST_MATCH_LIST_ENTRY,
                    gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s',game:'%s'}",
                            entry.getKey().substring(0, entry.getKey().indexOf("-")),
                            teams,
                            simpleDateFormat.format(entry.getValue().stream().findAny().get().getPlayDate()),
                            entry.getValue().stream().findAny().get().getGameName()), Map.class)));
        });
    }
}
