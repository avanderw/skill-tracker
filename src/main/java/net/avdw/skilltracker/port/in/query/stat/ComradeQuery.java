package net.avdw.skilltracker.port.in.query.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;

/**
 * A Comrade is an Ally that:
 * - Has played together at least 3 times
 * - Has the highest play count together
 */
public interface ComradeQuery {
    Optional<Player> find(Player player);

    Optional<Player> find(Game game, Player player);
}
