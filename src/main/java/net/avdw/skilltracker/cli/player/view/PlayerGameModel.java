package net.avdw.skilltracker.cli.player.view;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.KeyValue;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Value @Builder
public class PlayerGameModel {
    @NonNull Contestant contestant;
    @NonNull Integer contestantRank;
    @NonNull Integer totalMatches;
    @NonNull LocalDate firstPlayedDate;
    @NonNull LocalDate lastPlayedDate;
    @NonNull @Singular List<KeyValue> stats;
    @NonNull @Singular Set<KeyValue> trophies;
    @NonNull @Singular Set<KeyValue> challenges;
    @NonNull @Singular Set<KeyValue> achievements;
    @NonNull @Singular Set<KeyValue> badges;
}
