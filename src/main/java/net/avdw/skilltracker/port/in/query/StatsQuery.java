package net.avdw.skilltracker.port.in.query;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Stat;

import java.util.List;

public interface StatsQuery {
    List<Stat> gameStatsForPlayer(Game game, Player player);
    List<Stat> playerStats(Player player);
    List<Stat> gameStats(Game game);
}
