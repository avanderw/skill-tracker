package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.stat.EnthusiastQuery;
import net.avdw.skilltracker.port.in.query.stat.ObsessionQuery;
import net.avdw.skilltracker.port.out.ContestantRepo;
import net.avdw.skilltracker.port.out.GameRepo;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnthusiastService implements EnthusiastQuery, ObsessionQuery {
    private final ContestantRepo contestantRepo;
    private final GameRepo gameRepo;

    @Inject
    public EnthusiastService(ContestantRepo contestantRepo, GameRepo gameRepo) {
        this.contestantRepo = contestantRepo;
        this.gameRepo = gameRepo;
    }

    @Override
    public Optional<Player> findEnthusiast(Game game) {
        List<Contestant> contestants = contestantRepo.contestantsFor(game).stream()
                .filter(c -> c.getPlayCount() >= 3)
                .sorted(Comparator.comparing(Contestant::getPlayCount).reversed())
                .collect(Collectors.toList());

        if (contestants.size() < 2) {
            return Optional.empty();
        } else {
            Contestant a = contestants.get(0);
            Contestant b = contestants.get(1);
            if (a.getPlayCount() * .75 > b.getPlayCount()) {
                return Optional.of(a.getPlayer());
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Game> findObsession(Player player) {
        return gameRepo.findGamesFor(player).stream()
                .filter(game -> findEnthusiast(game).stream()
                        .anyMatch(player::equals))
                .collect(Collectors.toList());
    }
}
