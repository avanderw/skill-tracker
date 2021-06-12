package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value @Builder
public class Game {
    @NonNull String name;
}
