package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value @Builder
public class WinStreak {
    @NonNull Integer current;
    @NonNull Integer longest;
}
