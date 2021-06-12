package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.domain.Player;

public interface RenamePlayer {
    void rename(Player from, String to);
}
