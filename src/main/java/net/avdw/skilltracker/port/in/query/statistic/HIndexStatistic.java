package net.avdw.skilltracker.port.in.query.statistic;

import net.avdw.skilltracker.domain.Player;

public interface HIndexStatistic {
    Integer getIndex(Player player);
}
