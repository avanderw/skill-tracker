package net.avdw.skilltracker.port.in.query.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Set;

/**
 * The Players that have this Player as a Nemesis.
 */
public interface MinionQuery {
    Set<Player> findAllMinions(Game game, Player player);
}
