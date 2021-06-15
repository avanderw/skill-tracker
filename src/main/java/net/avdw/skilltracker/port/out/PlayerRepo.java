package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlayerRepo {
    Optional<Player> findBy(String name);
    Set<Player> findBy(Game game);
    Set<Player> findAll();
}
