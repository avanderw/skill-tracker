package net.avdw.skilltracker.cli.game;

import net.avdw.skilltracker.cli.converter.GameTypeConverter;
import net.avdw.skilltracker.cli.game.view.GameDetailModel;
import net.avdw.skilltracker.cli.game.view.GameMatchModel;
import net.avdw.skilltracker.cli.game.view.GamePlayerModel;
import net.avdw.skilltracker.cli.game.view.GameDetailView;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Team;
import net.avdw.skilltracker.port.in.query.ContestantQuery;
import net.avdw.skilltracker.port.in.query.MatchQuery;
import net.avdw.skilltracker.port.in.query.RankQuery;
import net.avdw.skilltracker.port.in.query.StatsQuery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.stream.Collectors;

@Command(name = "view", description = "View the details of a game", mixinStandardHelpOptions = true)
public class ViewGameCommand implements Runnable {
    @Spec private CommandSpec spec;

    @Parameters(arity = "1", converter = GameTypeConverter.class)
    private Game game;

    @Inject private GameDetailView gameDetailView;
    @Inject private StatsQuery statsQuery;
    @Inject private RankQuery rankQuery;
    @Inject private MatchQuery matchQuery;
    @Inject private ContestantQuery contestantQuery;

    @Override
    public void run() {
        spec.commandLine().getOut().println(gameDetailView.render(GameDetailModel.builder()
                .topPlayers(contestantQuery.topContestantsBySkill(game, 5L).stream()
                        .map(c -> GamePlayerModel.builder()
                                .name(c.getPlayer().getName())
                                .position(rankQuery.findBy(game, c.getPlayer()))
                                .mean(c.getSkill().getMean())
                                .stdDev(c.getSkill().getStdDev())
                                .build())
                        .collect(Collectors.toList()))
                .gameStats(statsQuery.gameStats(game))
                .matches(matchQuery.findLastBy(game, 3L).stream()
                        .map(m -> GameMatchModel.builder()
                                .date(m.getDate())
                                .sessionId(m.getSessionId())
                                .title(m.getTeams().stream()
                                        .sorted(Comparator.comparing(Team::getRank))
                                        .map(t -> String.format("#%s:%s", t.getRank(), t.getContestants().stream()
                                                .map(c -> c.getPlayer().getName())
                                                .collect(Collectors.joining(" & "))))
                                        .collect(Collectors.joining(" vs. ")))
                                .build())
                        .collect(Collectors.toList()))
                .build()));
    }
}
