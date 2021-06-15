package net.avdw.skilltracker.cli.player.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import net.avdw.skilltracker.domain.Player;

import java.util.Set;

@Value @Builder
public class PlayerListModel {
    @NonNull Set<Player> players;
}
