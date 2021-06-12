package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value @Builder
public class Matchup {
    @NonNull Player player;
    @NonNull Player opponent;
    @NonNull Integer opponentWinCount;
    @NonNull Integer totalPlayCount;

    public double getOpponentWinRatio() {
        return opponentWinCount.doubleValue() / totalPlayCount.doubleValue();
    }
}
