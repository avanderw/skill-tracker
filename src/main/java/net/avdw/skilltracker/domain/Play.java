package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;

@Value @Builder
public class Play {
    @NonNull Player player;
    @NonNull Game game;
    @NonNull LocalDate date;
}
