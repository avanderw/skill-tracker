package net.avdw.skilltracker.port.in.query.achievement;

import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;

import java.util.List;

public interface AllAchievements {
    List<KeyValue> forPlayer(Player player);
}
