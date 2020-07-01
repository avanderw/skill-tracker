package net.avdw.skilltracker.match;

import lombok.Data;
import net.avdw.skilltracker.player.PlayerTable;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class TeamData {
    private final Set<PlayerTable> playerTableSet = new LinkedHashSet<>();

    public void add(final PlayerTable name) {
        playerTableSet.add(name);
    }
}
