package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Command(name = "view", description = "View match information", mixinStandardHelpOptions = true)
public class RetrieveMatchCli implements Runnable {
    private final Gson gson = new Gson();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Parameters(arity = "1")
    private String id;
    @Inject
    private MatchService matchService;
    @Spec
    private CommandSpec spec;
    @Inject
    @MatchScope
    private Templator templator;

    @Override
    public void run() {
        List<PlayEntity> ormLiteMatchList = matchService.retrieveMatchWithSessionId(id);

        if (ormLiteMatchList.isEmpty()) {
            templator.populate(MatchBundleKey.NO_MATCH_FOUND);
            return;
        }

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.VIEW_MATCH_DETAIL_TITLE,
                gson.fromJson(String.format("{id:'%s',date:'%s',game:'%s'}", id,
                        simpleDateFormat.format(ormLiteMatchList.stream().findAny().get().getPlayDate()),
                        ormLiteMatchList.stream().findAny().get().getGameName()),
                        Map.class)));

        ormLiteMatchList.stream().sorted(Comparator.comparingInt(PlayEntity::getTeamRank)).forEach(m ->
                spec.commandLine().getOut().println(templator.populate(MatchBundleKey.VIEW_MATCH_DETAIL_PLAYER_ENTRY,
                        gson.fromJson(String.format("{rank:'%s',person:'%s',mean:'%s',stdev:'%s'}",
                                m.getTeamRank(), m.getPlayerName(),
                                m.getPlayerMean().setScale(0, RoundingMode.HALF_UP),
                                m.getPlayerStdDev().setScale(0, RoundingMode.HALF_UP)),
                                Map.class))));
    }
}
