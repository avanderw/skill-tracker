package net.avdw.skilltracker.port.in.query.badge;

import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;

import java.util.Collection;
import java.util.List;

public interface AllBadges {
    List<KeyValue> forPlayer(Player player);
}
