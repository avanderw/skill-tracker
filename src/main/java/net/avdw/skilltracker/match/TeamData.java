package net.avdw.skilltracker.match;

import lombok.Data;
import net.avdw.skilltracker.player.PlayerTable;

import java.util.HashSet;
import java.util.Set;

@Data
class TeamData {
    private final Set<PlayerTable> playerTableSet = new HashSet<>();

    public void add(final PlayerTable name) {
        playerTableSet.add(name);
    }
}
