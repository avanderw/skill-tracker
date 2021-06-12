package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Set;

public interface MinionQuery {
    Set<Player> findAll(Game game, Player player);
}
