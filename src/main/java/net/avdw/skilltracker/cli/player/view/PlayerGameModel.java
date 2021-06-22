package net.avdw.skilltracker.cli.player.view;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.KeyValue;

import java.util.List;

@Value @Builder
public class PlayerGameModel {
    @NonNull Contestant contestant;
    @NonNull Integer contestantRank;
    @NonNull Integer totalMatches;
    @NonNull @Singular List<KeyValue> stats;
}
