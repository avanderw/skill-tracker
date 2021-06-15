package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.RankQuery;
import net.avdw.skilltracker.port.out.RankRepo;

import javax.inject.Inject;

public class RankService implements RankQuery {
    private final RankRepo rankRepository;

    @Inject
    public RankService(RankRepo rankRepository) {
        this.rankRepository = rankRepository;
    }

    @Override
    public Integer findBy(Game game, Player player) {
        return rankRepository.findBy(game, player);
    }
}
