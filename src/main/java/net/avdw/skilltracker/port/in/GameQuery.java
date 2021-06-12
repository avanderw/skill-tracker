package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;
import java.util.Set;

public interface GameQuery {
    Optional<Game> findAll(String name);
    Set<Game> findAll(Player player);
    Integer totalGames(Player player);
    Game lastPlayed(Player player);
}
