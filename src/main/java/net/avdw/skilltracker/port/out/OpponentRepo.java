package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Set;

public interface OpponentRepo {
    Set<Player> findBy(Game game, Player player);

    Integer findOpponentWinCount(Player player, Player opponent, Game game);

    Integer findTotalPlayCount(Player player, Player opponent, Game game);
}
