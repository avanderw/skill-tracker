package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Stat;
import net.avdw.skilltracker.port.in.query.StatsQuery;
import net.avdw.skilltracker.port.in.query.stat.*;
import net.avdw.skilltracker.port.out.ContestantRepo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatsService implements StatsQuery {
    private final MinionQuery minionQuery;
    private final NemesisQuery nemesisQuery;
    private final ComradeQuery comradeQuery;
    private final CamaraderieQuery camaraderieQuery;
    private final ContestantRepo contestantRepo;
    private final EnthusiastQuery enthusiastQuery;
    private final ObsessionQuery obsessionQuery;
    private final GuardianQuery guardianQuery;
    private final DominatorQuery dominatorQuery;

    @Inject
    public StatsService(MinionQuery minionQuery,
                        NemesisQuery nemesisQuery,
                        ComradeQuery comradeQuery,
                        CamaraderieQuery camaraderieQuery,
                        ContestantRepo contestantRepo,
                        EnthusiastQuery enthusiastQuery,
                        ObsessionQuery obsessionQuery, GuardianQuery guardianQuery, DominatorQuery dominatorQuery) {
        this.minionQuery = minionQuery;
        this.nemesisQuery = nemesisQuery;
        this.comradeQuery = comradeQuery;
        this.camaraderieQuery = camaraderieQuery;
        this.contestantRepo = contestantRepo;
        this.enthusiastQuery = enthusiastQuery;
        this.obsessionQuery = obsessionQuery;
        this.guardianQuery = guardianQuery;
        this.dominatorQuery = dominatorQuery;
    }

    @Override
    public List<Stat> gameStatsForPlayer(Game game, Player player) {
        List<Stat> stats = new ArrayList<>();

        nemesisQuery.findNemesis(game, player)
                .map(nemesis -> Stat.builder().name("Nemesis").value(nemesis.getName()).build())
                .ifPresent(stats::add);

        Set<Player> minions = minionQuery.findAllMinions(game, player);
        if (!minions.isEmpty()) {
            stats.add(Stat.builder().name("Minions").value(minions.stream()
                    .map(Player::getName)
                    .sorted()
                    .collect(Collectors.joining(", ")))
                    .build());
        }

        comradeQuery.findComrade(game, player)
                .map(p -> Stat.builder()
                        .name("Comrade")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        String camaraderie = camaraderieQuery.findAll(game, player).stream()
                .map(Player::getName)
                .sorted()
                .collect(Collectors.joining(", "));
        if (!camaraderie.isBlank()) {
            stats.add(Stat.builder()
                    .name("Camaraderie")
                    .value(camaraderie)
                    .build());
        }

        guardianQuery.findGuardian(game, player)
                .map(g -> Stat.builder()
                        .name("Guardian")
                        .value(g.getName())
                        .build())
                .ifPresent(stats::add);
        String wards = guardianQuery.findWards(game, player).stream()
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

        Contestant mostPlayed = contestantRepo.mostPlayed(player);
        stats.add(Stat.builder()
                .name("Most played")
                .value(String.format("%s", mostPlayed.getGame().getName()))
                .build());

        comradeQuery.findComrade(player)
                .map(p -> Stat.builder()
                        .name("Comrade")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        String camaraderie = camaraderieQuery.findAll(player).stream()
                .map(Player::getName)
                .sorted()
                .collect(Collectors.joining(", "));
        if (!camaraderie.isBlank()) {
            stats.add(Stat.builder()
                    .name("Camaraderie")
                    .value(camaraderie)
                    .build());
        }

        String obsession = obsessionQuery.findObsession(player).stream()
                .map(Game::getName)
                .collect(Collectors.joining(", "));
        if (!obsession.isBlank()) {
            stats.add(Stat.builder()
                    .name("Obsession")
                    .value(obsession)
                    .build());
        }

        String dominating = dominatorQuery.findDominating(player).stream()
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

        Contestant mostWins = contestantRepo.mostWinsForGame(game);
        stats.add(Stat.builder()
                .name("Most wins")
                .value(String.format("%s", mostWins.getPlayer().getName()))
                .build());

        enthusiastQuery.findEnthusiast(game)
                .map(p -> Stat.builder()
                        .name("Enthusiast")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        dominatorQuery.findDominator(game)
                .map(p -> Stat.builder()
                        .name("Dominator")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        return stats;
    }
}
