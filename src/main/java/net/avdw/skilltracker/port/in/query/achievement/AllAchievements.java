package net.avdw.skilltracker.port.in.query.achievement;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;

import java.util.List;

public interface AllAchievements {
    List<KeyValue> findFor(Player player);
    List<KeyValue> findFor(Game game, Player player);
}
