package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Match;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.MatchQuery;
import net.avdw.skilltracker.port.out.MatchRepo;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

public class MatchService implements MatchQuery {
    private final MatchRepo matchRepo;

    @Inject
    public MatchService(MatchRepo matchRepo) {
        this.matchRepo = matchRepo;
    }

    @Override
    public Integer totalMatches(Game game, Player player) {
        return matchRepo.totalMatches(game, player);
    }

    @Override
    public Integer totalMatches(Player player) {
        return matchRepo.totalMatches(player);
    }

    @Override
    public LocalDate lastPlayedDate(Player player) {
        return matchRepo.lastPlayedDate(player);
    }

    @Override
    public List<Match> findLastBy(Game game, Long limit) {
        return matchRepo.findLastBy(game, limit);
    }
}
