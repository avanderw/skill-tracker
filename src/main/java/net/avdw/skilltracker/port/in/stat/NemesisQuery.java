package net.avdw.skilltracker.port.in.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;

/**
 * A nemesis is an opponent:
 * - beaten the player at least 3 times
 * - has at least 50% win ratio
 * - has the highest win ratio
 */
public interface NemesisQuery {
    Optional<Player> find(Game game, Player player);
}
