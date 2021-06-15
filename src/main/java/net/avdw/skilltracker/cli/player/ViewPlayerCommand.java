package net.avdw.skilltracker.cli.player;

import net.avdw.skilltracker.cli.converter.GameTypeConverter;
import net.avdw.skilltracker.cli.converter.PlayerTypeConverter;
import net.avdw.skilltracker.cli.player.model.ContestantModel;
import net.avdw.skilltracker.cli.player.model.PlayerDetailModel;
import net.avdw.skilltracker.cli.player.view.ContestantView;
import net.avdw.skilltracker.cli.player.view.PlayerDetailView;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.PriorityObject;
import net.avdw.skilltracker.port.in.query.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "view", description = "View player detail", mixinStandardHelpOptions = true)
public class ViewPlayerCommand implements Runnable {
    @Spec private CommandSpec spec;
    @Parameters(paramLabel = "name", arity = "1", converter = PlayerTypeConverter.class)
    private Player player;
    @Option(names = {"-g", "--game"}, converter = GameTypeConverter.class)
    private Game game;

    @Inject private ContestantView contestantView;
    @Inject private PlayerDetailView playerDetailView;
    @Inject private StatsQuery statsQuery;
    @Inject private SkillQuery skillQuery;
    @Inject private GameQuery gameQuery;
    @Inject private RankQuery rankQuery;
    @Inject private MatchQuery matchQuery;
    @Inject private ContestantQuery contestantQuery;

    @Inject
    ViewPlayerCommand() {
    }

    @Override
    public void run() {
        if (game == null) {
            List<Game> games = new ArrayList<>(gameQuery.findAll(player));
            PlayerDetailModel playerDetailModel = PlayerDetailModel.builder()
                    .player(player)
                    .lastPlayedGame(gameQuery.lastPlayed(player))
                    .lastPlayedDate(matchQuery.lastPlayedDate(player))
                    .totalGames(gameQuery.totalGames(player))
                    .totalMatches(matchQuery.totalMatches(player))
                    .rankedGames(games.stream()
                            .map(g -> PriorityObject.<Game>builder()
                                    .object(g)
                                    .priority(rankQuery.findBy(g, player))
                                    .build())
                            .sorted(Comparator.comparing(g -> g.getPriority().intValue()))
                            .limit(3)
                            .collect(Collectors.toList()))
                    .skilledGames(games.stream()
                            .map(g -> PriorityObject.<Game>builder()
                                    .object(g)
                                    .priority(skillQuery.findLatest(g, player)
                                            .getLow())
                                    .build())
                            .sorted(Comparator.comparing((PriorityObject<Game> g) -> g.getPriority().doubleValue()).reversed())
                            .limit(3)
                            .collect(Collectors.toList()))
                    .allStats(statsQuery.playerStats(player))
                    .build();
            spec.commandLine().getOut().println(playerDetailView.render(playerDetailModel));
        } else {
            ContestantModel contestantModel = ContestantModel.builder()
                    .contestant(contestantQuery.findContestant(game, player))
                    .contestantRank(rankQuery.findBy(game, player))
                    .totalMatches(matchQuery.totalMatches(game, player))
                    .stats(statsQuery.gameStatsForPlayer(game, player))
                    .build();
            spec.commandLine().getOut().println(contestantView.render(contestantModel));
        }
    }
}
