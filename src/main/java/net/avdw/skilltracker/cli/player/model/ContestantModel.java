package net.avdw.skilltracker.cli.player.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Stat;

import java.util.List;

@Value @Builder
public class ContestantModel {
    @NonNull Contestant contestant;
    @NonNull Integer contestantRank;
    @NonNull Integer totalMatches;
    @NonNull @Singular List<Stat> stats;
}
