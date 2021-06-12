package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Stat;
import net.avdw.skilltracker.port.in.MinionQuery;
import net.avdw.skilltracker.port.in.NemesisQuery;
import net.avdw.skilltracker.port.in.StatsQuery;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatsService implements StatsQuery {
    private final MinionQuery minionQuery;
    private final NemesisQuery nemesisQuery;

    @Inject
    public StatsService(MinionQuery minionQuery, NemesisQuery nemesisQuery) {
        this.minionQuery = minionQuery;
        this.nemesisQuery = nemesisQuery;
    }

    @Override
    public List<Stat> allGameStatsForPlayer(Game game, Player player) {
        List<Stat> stats = new ArrayList<>();

        nemesisQuery.find(game, player)
                .map(nemesis->Stat.builder().name("Nemesis").value(nemesis.getName()).build())
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
        Logger.warn("No stats configured for player games.");
        return new ArrayList<>();
    }
}
