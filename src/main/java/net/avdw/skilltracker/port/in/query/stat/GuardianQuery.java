package net.avdw.skilltracker.port.in.query.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;
import java.util.Optional;

/**
 * A Guardian is an Ally that:
 * - Has won together at least 3 times
 * - Has a higher skill
 * - Has at least a 50% win ratio together
 * - Has the highest win ratio
 */
public interface GuardianQuery {
    Optional<Player> findGuardian(Game game, Player player);
    List<Player> findWards(Game game, Player player);
}
