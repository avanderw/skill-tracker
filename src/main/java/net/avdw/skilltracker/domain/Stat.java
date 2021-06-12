package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class Stat {
    String name;
    String value;
}
