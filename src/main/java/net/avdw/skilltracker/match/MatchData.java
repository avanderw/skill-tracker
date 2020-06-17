package net.avdw.skilltracker.match;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
class MatchData {
    private final Set<TeamData> teamDataSet = new HashSet<>();
    private BigDecimal quality;

    public void add(final TeamData team) {
        teamDataSet.add(team);
    }
}
