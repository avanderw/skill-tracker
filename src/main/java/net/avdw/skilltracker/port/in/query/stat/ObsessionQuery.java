package net.avdw.skilltracker.port.in.query.stat;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;

/**
 * The Games that have this Player as an Enthusiast.
 */
public interface ObsessionQuery {
    List<Game> findObsession(Player player);
}
