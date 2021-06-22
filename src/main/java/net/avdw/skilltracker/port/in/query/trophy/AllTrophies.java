package net.avdw.skilltracker.port.in.query.trophy;

import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;

import java.util.List;

public interface AllTrophies {
    List<KeyValue> forPlayer(Player player);
}
