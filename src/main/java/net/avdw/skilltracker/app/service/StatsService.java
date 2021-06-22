package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Stat;
import net.avdw.skilltracker.port.in.query.StatsQuery;
import net.avdw.skilltracker.port.in.query.achievement.GuardianAchievement;
import net.avdw.skilltracker.port.in.query.achievement.NemesisAchievement;
import net.avdw.skilltracker.port.in.query.badge.ComradeBadge;
import net.avdw.skilltracker.port.in.query.badge.EnthusiastBadge;
import net.avdw.skilltracker.port.in.query.trophy.DominatorTrophy;
import net.avdw.skilltracker.port.out.ContestantRepo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatsService implements StatsQuery {
    private final NemesisAchievement nemesisAchievement;
    private final ComradeBadge comradeBadge;
    private final ContestantRepo contestantRepo;
    private final EnthusiastBadge enthusiastBadge;
    private final GuardianAchievement guardianAchievement;
    private final DominatorTrophy dominatorTrophy;

    @Inject
    public StatsService(                        NemesisAchievement nemesisAchievement,
                        ComradeBadge comradeBadge,
                        ContestantRepo contestantRepo,
                        EnthusiastBadge enthusiastBadge,
                        GuardianAchievement guardianAchievement,
                        DominatorTrophy dominatorTrophy) {
        this.nemesisAchievement = nemesisAchievement;
        this.comradeBadge = comradeBadge;
        this.contestantRepo = contestantRepo;
        this.enthusiastBadge = enthusiastBadge;
        this.guardianAchievement = guardianAchievement;
        this.dominatorTrophy = dominatorTrophy;
    }

    @Override
    public List<Stat> gameStatsForPlayer(Game game, Player player) {
        List<Stat> stats = new ArrayList<>();

        nemesisAchievement.findNemesis(game, player)
                .map(nemesis -> Stat.builder().name("Nemesis").value(nemesis.getName()).build())
                .ifPresent(stats::add);

        Set<Player> minions = nemesisAchievement.findAllMinions(game, player);
        if (!minions.isEmpty()) {
            stats.add(Stat.builder().name("Minions").value(minions.stream()
                    .map(Player::getName)
                    .sorted()
                    .collect(Collectors.joining(", ")))
                    .build());
        }

        comradeBadge.findComrade(game, player)
                .map(p -> Stat.builder()
                        .name("Comrade")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        guardianAchievement.findGuardian(game, player)
                .map(g -> Stat.builder()
                        .name("Guardian")
                        .value(g.getName())
                        .build())
                .ifPresent(stats::add);
        String wards = guardianAchievement.findWards(game, player).stream()
                .map(Player::getName)
                .sorted()
                .collect(Collectors.joining(", "));
        if (!wards.isBlank()) {
            stats.add(Stat.builder()
                    .name("Wards")
                    .value(wards)
                    .build());
        }

        return stats;
    }

    @Override
    public List<Stat> playerStats(Player player) {
        List<Stat> stats = new ArrayList<>();

        comradeBadge.findComrade(player)
                .map(p -> Stat.builder()
                        .name("Comrade")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        String obsession = enthusiastBadge.findObsession(player).stream()
                .map(Game::getName)
                .collect(Collectors.joining(", "));
        if (!obsession.isBlank()) {
            stats.add(Stat.builder()
                    .name("Obsession")
                    .value(obsession)
                    .build());
        }

        String dominating = dominatorTrophy.findDominating(player).stream()
                .map(Game::getName)
                .sorted()
                .collect(Collectors.joining(", "));
        if (!dominating.isBlank()) {
            stats.add(Stat.builder()
                    .name("Dominating")
                    .value(dominating)
                    .build());
        }

        return stats;
    }

    @Override
    public List<Stat> gameStats(Game game) {
        List<Stat> stats = new ArrayList<>();

        enthusiastBadge.findEnthusiast(game)
                .map(p -> Stat.builder()
                        .name("Enthusiast")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        dominatorTrophy.findDominator(game)
                .map(p -> Stat.builder()
                        .name("Dominator")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        return stats;
    }

}
