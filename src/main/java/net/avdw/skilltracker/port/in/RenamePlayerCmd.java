package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.domain.Player;

public interface RenamePlayerCmd {
    void rename(Player from, String to);
}
