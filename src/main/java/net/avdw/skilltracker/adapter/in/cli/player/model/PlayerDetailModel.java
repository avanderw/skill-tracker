package net.avdw.skilltracker.adapter.in.cli.player.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.PriorityObject;
import net.avdw.skilltracker.domain.Stat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Value
@Builder
public class PlayerDetailModel {
    @NonNull Player player;
    @NonNull Integer totalGames;
    @NonNull Integer totalMatches;
    @NonNull Game lastPlayedGame;
    @NonNull LocalDate lastPlayedDate;
    @NonNull List<PriorityObject<Game>> rankedGames;
    @NonNull List<PriorityObject<Game>> skilledGames;
    @NonNull @Singular Set<Stat> allStats;
}
