package net.avdw.skilltracker.port.in.query.statistic;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.time.LocalDate;

public interface LastPlayedStatistic {
    Game lookupLastGameFor(Player player);

    LocalDate lookupLastDateFor(Player player);
    LocalDate lookupLastDateFor(Game game, Player player);
}
