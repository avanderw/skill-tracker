package net.avdw.skilltracker.port.in.query.statistic;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.time.LocalDate;

public interface FirstPlayedStatistic {
    Game lookupFirstGameFor(Player player);

    LocalDate lookupFirstDateFor(Player player);
    LocalDate lookupFirstDateFor(Game game, Player player);
}
