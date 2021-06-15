package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Stat;
import net.avdw.skilltracker.port.in.query.stat.CamaraderieQuery;
import net.avdw.skilltracker.port.in.query.stat.ComradeQuery;
import net.avdw.skilltracker.port.in.query.stat.MinionQuery;
import net.avdw.skilltracker.port.in.query.stat.NemesisQuery;
import net.avdw.skilltracker.port.in.query.StatsQuery;
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

    @Inject
    public StatsService(MinionQuery minionQuery, NemesisQuery nemesisQuery, ComradeQuery comradeQuery, CamaraderieQuery camaraderieQuery, ContestantRepo contestantRepo) {
        this.minionQuery = minionQuery;
        this.nemesisQuery = nemesisQuery;
        this.comradeQuery = comradeQuery;
        this.camaraderieQuery = camaraderieQuery;
        this.contestantRepo = contestantRepo;
    }

    @Override
    public List<Stat> gameStatsForPlayer(Game game, Player player) {
        List<Stat> stats = new ArrayList<>();

        nemesisQuery.find(game, player)
                .map(nemesis -> Stat.builder().name("Nemesis").value(nemesis.getName()).build())
                .ifPresent(stats::add);

        Set<Player> minions = minionQuery.findAll(game, player);
        if (!minions.isEmpty()) {
            stats.add(Stat.builder().name("Minions").value(minions.stream()
                    .map(Player::getName)
                    .sorted()
                    .collect(Collectors.joining(", ")))
                    .build());
        }

        comradeQuery.find(game, player)
                .map(p->Stat.builder()
                        .name("Comrade")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        String camaraderie = camaraderieQuery.findAll(game, player).stream()
                .map(Player::getName)
                .collect(Collectors.joining(", "));
        if (!camaraderie.isBlank()) {
            stats.add(Stat.builder()
                    .name("Camaraderie")
                    .value(camaraderie)
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
                .value(String.format("%s (%s)", mostPlayed.getGame().getName(), mostPlayed.getPlayCount()))
                .build());

        comradeQuery.find(player)
                .map(p->Stat.builder()
                        .name("Comrade")
                        .value(p.getName())
                        .build())
                .ifPresent(stats::add);

        String camaraderie = camaraderieQuery.findAll(player).stream()
                .map(Player::getName)
                .collect(Collectors.joining(", "));
        if (!camaraderie.isBlank()) {
            stats.add(Stat.builder()
                    .name("Camaraderie")
                    .value(camaraderie)
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
                .value(String.format("%s (%d)", mostWins.getPlayer().getName(), mostWins.getWinCount()))
                .build());
        return stats;
    }
}
