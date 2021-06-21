package net.avdw.skilltracker.cli.game.view;

import lombok.Builder;
import lombok.Value;
import net.avdw.skilltracker.domain.Game;

import java.util.List;

@Value @Builder
public class GameListModel {
    List<Game> games;
}
