package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
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
    @Match
    private Templator templator;

    @Override
    public void run() {
        List<MatchTable> matchTableList = matchService.retrieveMatchWithSessionId(id);

        if (matchTableList.isEmpty()) {
            templator.populate(MatchBundleKey.NO_MATCH_FOUND);
            return;
        }

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.VIEW_MATCH_DETAIL_TITLE,
                gson.fromJson(String.format("{id:'%s',date:'%s',game:'%s'}", id,
                        simpleDateFormat.format(matchTableList.stream().findAny().get().getPlayDate()),
                        matchTableList.stream().findAny().get().getGameTable().getName()),
                        Map.class)));

        matchTableList.stream().sorted(Comparator.comparingInt(MatchTable::getRank)).forEach(m ->
                spec.commandLine().getOut().println(templator.populate(MatchBundleKey.VIEW_MATCH_DETAIL_PLAYER_ENTRY,
                        gson.fromJson(String.format("{rank:'%s',person:'%s',mean:'%s',stdev:'%s'}",
                                m.getRank(), m.getPlayerTable().getName(),
                                m.getMean().setScale(0, RoundingMode.HALF_UP),
                                m.getStandardDeviation().setScale(0, RoundingMode.HALF_UP)),
                                Map.class))));
    }
}
