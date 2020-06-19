package net.avdw.skilltracker.match;

import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
class MatchData {
    private final Set<TeamData> teamDataSet = new LinkedHashSet<>();
    private BigDecimal quality;

    public void add(final TeamData team) {
        teamDataSet.add(team);
    }
}
