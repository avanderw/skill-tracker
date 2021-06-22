package net.avdw.skilltracker.port.in.query;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;

import java.util.List;

public interface StatsQuery {
    List<KeyValue> gameStatsForPlayer(Game game, Player player);
    List<KeyValue> playerStats(Player player);
    List<KeyValue> gameStats(Game game);
}
