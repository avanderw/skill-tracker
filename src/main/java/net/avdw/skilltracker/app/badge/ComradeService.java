package net.avdw.skilltracker.app.badge;

import net.avdw.skilltracker.domain.Ally;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.badge.ComradeBadge;
import net.avdw.skilltracker.port.out.AllyRepo;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.Optional;

public class ComradeService implements ComradeBadge {
    private final AllyRepo allyRepo;

    @Inject
    public ComradeService(AllyRepo allyRepo) {
        this.allyRepo = allyRepo;
    }

    @Override
    public Optional<Player> findComrade(Player player) {
        return allyRepo.findAll(player).stream()
                .filter(a->a.getPlayCount() >= 3)
                .max(Comparator.comparing(Ally::getPlayCount))
                .map(Ally::getAlly);
    }

    @Override
    public Optional<Player> findComrade(Game game, Player player) {
        return allyRepo.findAll(game, player).stream()
                .filter(a->a.getPlayCount() >= 3)
                .max(Comparator.comparing(Ally::getPlayCount))
                .map(Ally::getAlly);
    }
}
