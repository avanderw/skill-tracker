package net.avdw.skilltracker.cli.game.view;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value @Builder
public class GamePlayerModel {
    @NonNull Integer position;
    @NonNull String name;
    @NonNull BigDecimal mean;
    @NonNull BigDecimal stdDev;
}
