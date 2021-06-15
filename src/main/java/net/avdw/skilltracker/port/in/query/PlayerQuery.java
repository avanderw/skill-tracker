package net.avdw.skilltracker.port.in.query;

import net.avdw.skilltracker.cli.game.model.GamePlayerModel;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlayerQuery {
    Optional<Player> findByName(String name);
    Set<Player> findAll();
}
