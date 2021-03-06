package net.avdw.skilltracker.cli.player.view;

import net.avdw.skilltracker.cli.converter.GameTypeConverter;
import net.avdw.skilltracker.cli.converter.PlayerTypeConverter;
import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.PriorityObject;
import net.avdw.skilltracker.port.in.query.*;
import net.avdw.skilltracker.port.in.query.achievement.AllAchievements;
import net.avdw.skilltracker.port.in.query.badge.AllBadges;
import net.avdw.skilltracker.port.in.query.challenge.AllChallenges;
import net.avdw.skilltracker.port.in.query.statistic.FirstPlayedStatistic;
import net.avdw.skilltracker.port.in.query.statistic.HIndexStatistic;
import net.avdw.skilltracker.port.in.query.statistic.LastPlayedStatistic;
import net.avdw.skilltracker.port.in.query.trophy.AllTrophies;
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

@Command(name = "view", description = "View player detail.", mixinStandardHelpOptions = true)
public class ViewPlayerCommand implements Runnable {
    @Spec private CommandSpec spec;
    @Parameters(paramLabel = "name", arity = "1", converter = PlayerTypeConverter.class)
    private Player player;
    @Option(names = {"-g", "--game"}, converter = GameTypeConverter.class)
    private Game game;

    @Inject private PlayerGameView playerGameView;
    @Inject private PlayerDetailView playerDetailView;
    @Inject private AllTrophies allTrophies;
    @Inject private AllChallenges allChallenges;
    @Inject private AllAchievements allAchievements;
    @Inject private AllBadges allBadges;
    @Inject private StatsQuery statsQuery;
    @Inject private SkillQuery skillQuery;
    @Inject private GameQuery gameQuery;
    @Inject private RankQuery rankQuery;
    @Inject private MatchQuery matchQuery;
    @Inject private ContestantQuery contestantQuery;
    @Inject private HIndexStatistic hIndexStatistic;
    @Inject private FirstPlayedStatistic firstPlayedStatistic;
    @Inject private LastPlayedStatistic lastPlayedStatistic;

    @Inject
    ViewPlayerCommand() {
    }

    @Override
    public void run() {
        if (game == null) {
            List<Game> games = new ArrayList<>(gameQuery.findAll(player));
            PlayerDetailModel playerDetailModel = PlayerDetailModel.builder()
                    .player(player)
                    .hIndex(hIndexStatistic.lookupIndex(player))
                    .firstPlayedGame(firstPlayedStatistic.lookupFirstGameFor(player))
                    .firstPlayedDate(firstPlayedStatistic.lookupFirstDateFor(player))
                    .lastPlayedGame(gameQuery.lastPlayed(player))
                    .lastPlayedDate(matchQuery.lastPlayedDate(player))
                    .mostPlayedGame(contestantQuery.mostPlayedGame(player))
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
                    .trophies(allTrophies.findFor(player))
                    .challenges(allChallenges.findFor(player))
                    .achievements(allAchievements.findFor(player))
                    .badges(allBadges.findFor(player))
                    .build();
            spec.commandLine().getOut().println(playerDetailView.render(playerDetailModel));
        } else {
            Contestant contestant = contestantQuery.findContestant(game, player);
            PlayerGameModel playerGameModel = PlayerGameModel.builder()
                    .lastPlayedDate(lastPlayedStatistic.lookupLastDateFor(game, player))
                    .firstPlayedDate(firstPlayedStatistic.lookupFirstDateFor(game, player))
                    .contestant(contestant)
                    .contestantRank(rankQuery.findBy(game, player))
                    .totalMatches(matchQuery.totalMatches(game, player))
                    .stats(statsQuery.gameStatsForPlayer(game, player))
                    .trophies(allTrophies.findFor(game, player))
                    .challenges(allChallenges.findFor(game, player))
                    .achievements(allAchievements.findFor(game, player))
                    .badges(allBadges.findFor(game, player))
                    .build();
            spec.commandLine().getOut().println(playerGameView.render(playerGameModel));
        }
    }
}
