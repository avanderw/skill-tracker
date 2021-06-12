package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value @Builder
public class Skill {
    @NonNull BigDecimal mean;
    @NonNull BigDecimal standardDeviation;
    @NonNull LocalDate date;

    public BigDecimal getLow() {
        return mean.subtract(standardDeviation.multiply(BigDecimal.valueOf(3)));
    }
}
