package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Set;

@Value @Builder
public class Team {
    @NonNull Integer rank;
    @NonNull Set<Contestant> contestants;

    public boolean hasPlayer(Player player) {
        return contestants.stream()
                .map(Contestant::getPlayer)
                .anyMatch(p->p.equals(player));
    }

    public boolean isWinner() {
        return rank == 1;
    }

    public Contestant getContestant(Player player) {
        return contestants.stream()
                .filter(c->c.getPlayer().equals(player))
                .findFirst()
                .orElseThrow();
    }
}
