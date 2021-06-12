package net.avdw.skilltracker.adapter.in.cli.player.model;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class MovePlayerModel {
    String from;
    String to;
    boolean same;
    boolean merge;
}
