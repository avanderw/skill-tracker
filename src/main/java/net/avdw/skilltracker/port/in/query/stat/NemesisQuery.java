package net.avdw.skilltracker.port.in.query.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;

/**
 * A Nemesis is an Opponent that:
 * - Has beaten the player at least 3 times
 * - Has at least 50% win ratio
 * - Has the highest win ratio
 */
public interface NemesisQuery {
    Optional<Player> find(Game game, Player player);
}
