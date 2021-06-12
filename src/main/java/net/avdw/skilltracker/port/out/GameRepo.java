package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.Optional;
import java.util.Set;

public interface GameRepo {
    Set<Game> findBy(Player player);
    Optional<Game> findBy(String name);
    Integer totalGames(Player player);
    Game lastPlayed(Player player);
}
