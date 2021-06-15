package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value @Builder
public class Stat {
    @NonNull String name;
    @NonNull String value;
}
