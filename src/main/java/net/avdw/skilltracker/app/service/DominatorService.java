package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.stat.DominatorQuery;
import net.avdw.skilltracker.port.out.ContestantRepo;
import net.avdw.skilltracker.port.out.GameRepo;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DominatorService implements DominatorQuery {
    private final ContestantRepo contestantRepo;
    private final GameRepo gameRepo;

    @Inject
    public DominatorService(ContestantRepo contestantRepo, GameRepo gameRepo) {
        this.contestantRepo = contestantRepo;
        this.gameRepo = gameRepo;
    }

    @Override
    public Optional<Player> findDominator(Game game) {
        return contestantRepo.contestantsFor(game).stream()
                .filter(c->c.getWinCount() >= 3)
                .filter(c->c.getWinCount().doubleValue() / c.getPlayCount() >= 0.5)
                .sorted(Comparator.comparing((Contestant c) -> c.getSkill().getLow()).reversed())
                .map(Contestant::getPlayer)
                .findFirst();
    }

    @Override
    public List<Game> findDominating(Player player) {
        return gameRepo.findGamesFor(player).stream()
                .filter(g-> findDominator(g).filter(player::equals).isPresent())
                .collect(Collectors.toList());
    }
}
