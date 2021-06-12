package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.time.LocalDate;

public interface MatchQuery {
    Integer totalMatches(Game game, Player player);

    Integer totalMatches(Player player);

    LocalDate lastPlayedDate(Player player);
}
