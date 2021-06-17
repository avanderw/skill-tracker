package net.avdw.skilltracker.port.in.query;

import net.avdw.skilltracker.domain.Player;

import java.util.Optional;
import java.util.Set;

public interface PlayerQuery {
    Optional<Player> findByName(String name);
    Set<Player> findAll();
}
