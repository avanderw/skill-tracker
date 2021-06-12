package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.OpponentQuery;
import net.avdw.skilltracker.port.out.OpponentRepo;

import javax.inject.Inject;
import java.util.Set;

public class OpponentService implements OpponentQuery {
    private final OpponentRepo opponentRepo;

    @Inject
    public OpponentService(OpponentRepo opponentRepo) {
        this.opponentRepo = opponentRepo;
    }

    @Override
    public Set<Player> findBy(Game game, Player player) {
        return opponentRepo.findBy(game, player);
    }
}
