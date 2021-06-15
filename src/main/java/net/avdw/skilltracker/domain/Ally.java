package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.Value;

/**
 * A Player who has played on a Team
 */
@Value @Builder
public class Ally {
    Player player;
    Player ally;
    Long playCount;
    Long winCount;
}
