package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;

public interface NemesisQuery {
    Optional<Player> find(Game game, Player player);
}
