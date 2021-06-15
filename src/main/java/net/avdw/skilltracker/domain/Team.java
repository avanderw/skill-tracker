package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Set;

/**
 * A Team is a collection of Contestants on the same side in a game.
 * @see Contestant
 */
@Value @Builder
public class Team {
    @NonNull Integer rank;
    @NonNull List<Contestant> contestants;
}
