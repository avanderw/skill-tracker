package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Stat;
import net.avdw.skilltracker.port.in.stat.MinionQuery;
import net.avdw.skilltracker.port.in.stat.NemesisQuery;
import net.avdw.skilltracker.port.in.StatsQuery;
import net.avdw.skilltracker.port.out.ContestantRepo;
import net.avdw.skilltracker.port.out.GameRepo;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatsService implements StatsQuery {
    private final MinionQuery minionQuery;
    private final NemesisQuery nemesisQuery;
    private final ContestantRepo contestantRepo;

    @Inject
    public StatsService(MinionQuery minionQuery, NemesisQuery nemesisQuery, ContestantRepo contestantRepo) {
        this.minionQuery = minionQuery;
        this.nemesisQuery = nemesisQuery;
        this.contestantRepo = contestantRepo;
    }

    @Override
    public List<Stat> allGameStatsForPlayer(Game game, Player player) {
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
        return stats;
    }

    @Override
    public List<Stat> findBy(Player player) {
        List<Stat> stats = new ArrayList<>();

        Contestant mostPlayed = contestantRepo.mostPlayed(player);
        stats.add(Stat.builder()
                .name("Most played")
                .value(String.format("%s (%s)", mostPlayed.getGame().getName(), mostPlayed.getPlayCount()))
                .build());

        return stats;
    }

    @Override
    public List<Stat> findBy(Game game) {
        List<Stat> stats = new ArrayList<>();

        Contestant mostWins = contestantRepo.mostWinsForGame(game);
        stats.add(Stat.builder()
                .name("Most wins")
                .value(String.format("%s (%d)", mostWins.getPlayer().getName(), mostWins.getWinCount()))
                .build());
        return stats;
    }
}
