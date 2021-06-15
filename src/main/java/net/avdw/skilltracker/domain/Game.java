package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * A Game is something that can be played by a Player.
 * @see Player
 */
@Value @Builder
public class Game {
    @NonNull String name;
}
