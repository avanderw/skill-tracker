package net.avdw.skilltracker.cli.game.view;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import net.avdw.skilltracker.domain.KeyValue;

import java.util.List;

@Value @Builder
public class GameDetailModel {
    @NonNull List<GamePlayerModel> topPlayers;
    @NonNull List<GameMatchModel> matches;
    @NonNull List<KeyValue> gameKeyValues;
}
