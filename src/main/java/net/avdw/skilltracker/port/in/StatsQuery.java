package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Stat;

import java.util.List;

public interface StatsQuery {
    List<Stat> allGameStatsForPlayer(Game game, Player player);
    List<Stat> findBy(Player player);
}
