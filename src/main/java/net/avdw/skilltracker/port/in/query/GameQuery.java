package net.avdw.skilltracker.port.in.query;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GameQuery {
    List<Game> findAll();
    Optional<Game> findAll(String name);
    Set<Game> findAll(Player player);
    Integer totalGames(Player player);
    Game lastPlayed(Player player);
    List<Game> findLike(String search);
}
