package net.avdw.skilltracker.port.in.query.trophy;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;
import java.util.Optional;

public interface DominatorTrophy extends Trophy {

    @Override
    default String getTitle() {
        return "Dominator";
    }

    @Override
    default String getDescription() {
        return "A Dominator is a Competitor that:\n" +
                "- Has at least 3 wins\n" +
                "- Has at least 50% win ratio\n" +
                "- Has the highest skill";
    }

    Optional<Player> findDominator(Game game);
    List<Game> findDominating(Player player);
}
