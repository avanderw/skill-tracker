package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

/**
 * A Match is an event in a Game where Teams compete against each other.
 * @see Team
 */
@Value @Builder
public class Match {
    @NonNull String sessionId;
    @NonNull LocalDate date;
    @NonNull List<Team> teams;
}
