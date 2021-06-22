package net.avdw.skilltracker.port.in.query.achievement;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;
import java.util.Optional;

public interface GuardianAchievement extends Achievement {

    @Override
    default String getTitle() {
        return "Guardian";
    }

    @Override
    default String getDescription() {
        return "A Guardian is an Ally that:\n" +
                "- Has won together at least 3 times\n" +
                "- Has a higher skill\n" +
                "- Has at least a 50% win ratio together\n" +
                "- Has the highest win ratio";
    }

    Optional<Player> findGuardian(Game game, Player player);
    List<Player> findWards(Game game, Player player);
}
