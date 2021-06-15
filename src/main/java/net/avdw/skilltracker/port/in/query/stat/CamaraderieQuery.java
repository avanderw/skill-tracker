package net.avdw.skilltracker.port.in.query.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;
import java.util.Optional;

/**
 * The Players that have this Player as a Comrade.
 */
public interface CamaraderieQuery {
    List<Player> findAll(Player player);

    List<Player> findAll(Game game, Player player);
}
