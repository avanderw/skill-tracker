package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.time.LocalDate;

public interface MatchRepo {
    Integer totalMatches(Game game, Player player);

    Integer totalMatches(Player player);

    LocalDate lastPlayedDate(Player player);
}
