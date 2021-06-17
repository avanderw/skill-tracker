package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A Skill is the descriptive statistic for a Contestant.
 * @see Contestant
 */
@Value @Builder
public class Skill {
    @NonNull BigDecimal mean;
    @NonNull BigDecimal stdDev;
    @NonNull LocalDate date;

    public BigDecimal getLow() {
        return mean.subtract(stdDev.multiply(BigDecimal.valueOf(3)));
    }
}
