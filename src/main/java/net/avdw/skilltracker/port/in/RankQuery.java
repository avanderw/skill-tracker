package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

public interface RankQuery {
    Integer findBy(Game game, Player player);
}
