package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

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
    private MatchDataBuilder matchDataBuilder;
    @Inject
    private MatchService matchService;
    @Option(names = {"-r", "--rank", "--ranks"}, split = ",", required = true)
    private int[] ranks;
    @Spec
    private CommandSpec spec;
    @Parameters(description = "Teams in the match; team=<player1,player2> (no spaces)", arity = "1..*")
    private List<String> teams;
    @Inject
    @MatchScope
    private Templator templator;

    @Override
    public void run() {
        if (teams == null) {
            throw new UnsupportedOperationException();
        }

        Logger.debug("Creating match");
        if (teams.size() == 1) {
            teams = Arrays.asList(teams.get(0).split(","));
        }
        if (ranks.length != teams.size()) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.TEAM_RANK_COUNT_MISMATCH));
        }

        OrmLiteGame ormLiteGame = new OrmLiteGame(game);
        MatchData matchData = matchDataBuilder.buildFromString(teams);
        List<ITeam> teamList = gameMatchTeamBuilder.build(ormLiteGame, matchData);
        List<PlayEntity> ormLiteMatchList = matchService.createMatchForGame(ormLiteGame, teamList, ranks);

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.CREATE_SUCCESS));
        ormLiteMatchList.stream().collect(Collectors.groupingBy(PlayEntity::getSessionId)).forEach((key, matchTables) -> {

            String teams = matchTables.stream().collect(Collectors.groupingBy(PlayEntity::getPlayerTeam)).values().stream()
                    .map(t -> {
                        String team = t.stream().map(matchTable -> templator.populate(MatchBundleKey.MATCH_TEAM_PLAYER_ENTRY,
                                gson.fromJson(String.format("{name:'%s'}",
                                        matchTable.getPlayerName()), Map.class)))
                                .sorted()
                                .collect(Collectors.joining(" & "));
                        team = templator.populate(MatchBundleKey.MATCH_TEAM_ENTRY,
                                gson.fromJson(String.format("{rank:'%s',team:'%s'}",
                                        t.stream().findAny().orElseThrow().getTeamRank() +1,
                                        team), Map.class));
                        return team;
                    })
                    .collect(Collectors.joining(" vs. "));

            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.LAST_MATCH_LIST_ENTRY,
                    gson.fromJson(String.format("{session:'%s',teams:'%s',date:'%s',game:'%s'}",
                            key.substring(0, key.indexOf("-")),
                            teams,
                            simpleDateFormat.format(ormLiteMatchList.stream().findAny().orElseThrow().getPlayDate()),
                            ormLiteMatchList.stream().findAny().orElseThrow().getGameName()), Map.class)));
        });
    }
}
