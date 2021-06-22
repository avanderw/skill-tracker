package net.avdw.skilltracker.port.in.query.badge;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;

public interface ComradeBadge extends Badge {

    @Override
    default String getTitle() {
        return "Comrade";
    }

    @Override
    default String getDescription() {
        return "A Comrade is an Ally that:\n" +
                "- Has played together at least 3 times\n" +
                "- Has the highest play count together";
    }

    Optional<Player> findComrade(Player player);

    Optional<Player> findComrade(Game game, Player player);
}
