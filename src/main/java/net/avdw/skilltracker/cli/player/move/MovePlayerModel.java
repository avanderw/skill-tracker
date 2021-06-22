package net.avdw.skilltracker.cli.player.move;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class MovePlayerModel {
    String from;
    String to;
    boolean same;
    boolean merge;
}
