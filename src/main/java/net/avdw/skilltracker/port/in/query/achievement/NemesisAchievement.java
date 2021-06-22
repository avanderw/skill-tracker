package net.avdw.skilltracker.port.in.query.achievement;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;
import java.util.Set;

public interface NemesisAchievement extends Achievement {
    @Override
    default String getTitle() {
        return "Nemesis";
    }

    @Override
    default String getDescription() {
        return "A Nemesis is an Opponent that:\n" +
                "- Has beaten the player at least 3 times\n" +
                "- Has at least 50% win ratio\n" +
                "- Has the highest win ratio";
    }

    Optional<Player> findNemesis(Game game, Player player);
    Set<Player> findAllMinions(Game game, Player player);
}
