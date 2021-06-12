package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Matchup;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.MatchupQuery;
import net.avdw.skilltracker.port.in.NemesisQuery;
import net.avdw.skilltracker.port.in.OpponentQuery;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class NemesisService implements NemesisQuery {
    private final MatchupQuery matchupQuery;
    private final OpponentQuery opponentQuery;

    @Inject
    NemesisService(MatchupQuery matchupQuery, OpponentQuery opponentQuery) {
        this.matchupQuery = matchupQuery;
        this.opponentQuery = opponentQuery;
    }

    @Override
    public Optional<Player> find(Game game, Player player) {
        final Set<Player> opponents = opponentQuery.findBy(game, player);
        final Set<Matchup> matchups = opponents.stream()
                .map(opponent-> matchupQuery.findBy(player, opponent, game))
                .collect(Collectors.toSet());
        final Queue<Matchup> nemesisQueue = new PriorityQueue<>(Comparator.comparing(Matchup::getOpponentWinRatio).reversed());
        nemesisQueue.addAll(matchups.stream()
                .filter(matchup -> matchup.getOpponentWinCount() >= 3)
                .filter(matchup -> matchup.getOpponentWinRatio() >= 0.5)
                .collect(Collectors.toSet()));

        return nemesisQueue.isEmpty() ? Optional.empty() : Optional.of(nemesisQueue.poll().getOpponent());
    }
}
