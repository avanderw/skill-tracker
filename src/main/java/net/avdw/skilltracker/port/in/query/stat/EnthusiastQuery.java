package net.avdw.skilltracker.port.in.query.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;

/**
 * An Enthusiast is a Competitor that:
 * - Has played at least 3 times
 * - Has the highest play count
 * - Has 25% more plays than the next person
 */
public interface EnthusiastQuery {
    Optional<Player> findEnthusiast(Game game);
}
