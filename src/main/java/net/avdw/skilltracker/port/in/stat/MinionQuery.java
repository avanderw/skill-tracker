package net.avdw.skilltracker.port.in.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Set;

/**
 * This is the opposite of the nemesis stat.
 * @see NemesisQuery
 */
public interface MinionQuery {
    Set<Player> findAll(Game game, Player player);
}
