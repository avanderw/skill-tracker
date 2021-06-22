package net.avdw.skilltracker.cli.player.view;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.PriorityObject;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Value
@Builder
public class PlayerDetailModel {
    @NonNull Player player;
    @NonNull Integer hIndex;
    @NonNull Integer totalGames;
    @NonNull Integer totalMatches;
    @NonNull Game lastPlayedGame;
    @NonNull Game firstPlayedGame;
    @NonNull Game mostPlayedGame;
    @NonNull LocalDate firstPlayedDate;
    @NonNull LocalDate lastPlayedDate;
    @NonNull List<PriorityObject<Game>> rankedGames;
    @NonNull List<PriorityObject<Game>> skilledGames;
    @NonNull @Singular Set<KeyValue> trophies;
    @NonNull @Singular Set<KeyValue> challenges;
    @NonNull @Singular Set<KeyValue> achievements;
    @NonNull @Singular Set<KeyValue> badges;
}
