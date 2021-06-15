package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Ally;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;

public interface AllyRepo {
    List<Ally> findAll(Game game, Player player);
    List<Ally> findAll(Player player);
}
