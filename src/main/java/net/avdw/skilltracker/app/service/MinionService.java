package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.MinionQuery;
import net.avdw.skilltracker.port.in.NemesisQuery;
import net.avdw.skilltracker.port.in.OpponentQuery;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

public class MinionService implements MinionQuery {
    private final NemesisQuery nemesisQuery;
    private final OpponentQuery opponentQuery;

    @Inject
    public MinionService(NemesisQuery nemesisQuery, OpponentQuery opponentQuery) {
        this.nemesisQuery = nemesisQuery;
        this.opponentQuery = opponentQuery;
    }

    @Override
    public Set<Player> findAll(Game game, Player player) {
        return opponentQuery.findBy(game, player).stream()
                .filter(opponent -> nemesisQuery.find(game, opponent).stream().anyMatch(player::equals))
                .collect(Collectors.toSet());
    }
}
