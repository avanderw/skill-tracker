package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

@Value @Builder
public class Match {
    @NonNull String sessionId;
    @NonNull LocalDate date;
    @NonNull Set<Team> teams;

    public boolean hasPlayer(Player player) {
        return teams.stream().anyMatch(team -> team.hasPlayer(player));
    }

    public boolean isWinner(Player player) {
        return teams.stream()
                .filter(Team::isWinner)
                .anyMatch(team -> team.hasPlayer(player));
    }

    public Team getTeam(Player player) {
        return teams.stream()
                .filter(team -> team.hasPlayer(player))
                .findAny().orElseThrow();
    }

}
