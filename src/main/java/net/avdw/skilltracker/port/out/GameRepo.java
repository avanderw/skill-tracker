package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GameRepo {
    Set<Game> findGamesFor(Player player);
    Optional<Game> findGamesFor(String name);
    Integer totalGames(Player player);
    Game lastPlayed(Player player);
    List<Game> findAll();
    List<Game> findLike(String search);
}
