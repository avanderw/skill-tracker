package net.avdw.skilltracker.port.in.query;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Set;

public interface OpponentQuery {
    Set<Player> findBy(Game game, Player player);
}
