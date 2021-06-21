package net.avdw.skilltracker.port.in.query.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;

public interface EnthusiastQuery extends GenericStatQuery {

    @Override
    default String getTitle() {
        return "Enthusiast";
    }

    @Override
    default String getDescription() {
        return "An Enthusiast is a Competitor that:\n" +
                "- Has played at least 3 times\n" +
                "- Has the highest play count\n" +
                "- Has 25% more plays than the next person";
    }

    Optional<Player> findEnthusiast(Game game);
}
