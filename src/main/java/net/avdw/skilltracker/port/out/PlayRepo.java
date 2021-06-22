package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Play;
import net.avdw.skilltracker.domain.Player;

import java.util.List;

public interface PlayRepo {
    List<Game> findAllGamesFor(Player player);

    Long lookupPlayCountFor(Game game, Player player);

    Play lookupFirstPlay(Player player);

}
