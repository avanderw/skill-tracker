package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

public interface RankRepo {
    Integer findBy(Game game, Player player);
}
