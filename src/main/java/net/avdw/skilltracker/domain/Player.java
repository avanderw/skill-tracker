package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * A Player is a person able to play a Game.
 * @see Game
 */
@Value @Builder
public class Player {
    @NonNull String name;
}
