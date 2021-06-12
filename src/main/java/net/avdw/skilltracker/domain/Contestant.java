package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value @Builder
public class Contestant {
    @NonNull Game game;
    @NonNull Player player;
    @NonNull Skill skill;
}
