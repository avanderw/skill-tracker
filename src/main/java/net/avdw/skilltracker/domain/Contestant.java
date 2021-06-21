package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * A Contestant is a Player in a Game.
 * @see Player
 * @see Game
 */
@Value @Builder
public class Contestant {
    @NonNull Game game;
    @NonNull Player player;
    @NonNull Skill skill;
    @NonNull Long winCount;
    @NonNull Long playCount;
    @NonNull WinStreak winStreak;
}
