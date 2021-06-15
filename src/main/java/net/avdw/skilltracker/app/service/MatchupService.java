package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Matchup;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.MatchupQuery;
import net.avdw.skilltracker.port.out.OpponentRepo;

import javax.inject.Inject;

public class MatchupService implements MatchupQuery {
    private final OpponentRepo opponentRepo;

    @Inject
    public MatchupService(OpponentRepo opponentRepo) {
        this.opponentRepo = opponentRepo;
    }

    @Override
    public Matchup findBy(Player player, Player opponent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Matchup findBy(Player player, Player opponent, Game game) {
        return Matchup.builder()
                .player(player)
                .opponent(opponent)
                .opponentWinCount(opponentRepo.findOpponentWinCount(player, opponent, game))
                .totalPlayCount(opponentRepo.findTotalPlayCount(player, opponent, game))
                .build();
    }
}
