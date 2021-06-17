package net.avdw.skilltracker.port.in.query.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;
import java.util.Optional;

/**
 * A Dominator is a Competitor that:
 * - Has at least 3 wins
 * - Has at least 50% win ratio
 * - Has the highest win ratio
 */
public interface DominatorQuery {
    Optional<Player> findDominator(Game game);
    List<Game> findDominating(Player player);
}
