package net.avdw.skilltracker.cli.game.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;

@Value @Builder
public class GameMatchModel {
    @NonNull String sessionId;
    @NonNull LocalDate date;
    @NonNull String title;
}
