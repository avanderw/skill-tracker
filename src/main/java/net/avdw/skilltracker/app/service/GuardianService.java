package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Ally;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.SkillQuery;
import net.avdw.skilltracker.port.in.query.stat.GuardianQuery;
import net.avdw.skilltracker.port.out.AllyRepo;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuardianService implements GuardianQuery {
    private final AllyRepo allyRepo;
    private final SkillQuery skillQuery;

    @Inject
    public GuardianService(AllyRepo allyRepo, SkillQuery skillQuery) {
        this.allyRepo = allyRepo;
        this.skillQuery = skillQuery;
    }

    @Override
    public Optional<Player> findGuardian(Game game, Player player) {
        return allyRepo.findAll(game, player).stream()
                .filter(a -> skillQuery.findLatest(game, a.getAlly()).getLow().compareTo(skillQuery.findLatest(game, player).getLow()) >= 0)
                .filter(a -> a.getWinCount() >= 3)
                .filter(a -> a.getWinCount().doubleValue() / a.getPlayCount() >= 0.5)
                .sorted(Comparator.comparing((Ally a) -> a.getWinCount().doubleValue() / a.getPlayCount()).reversed())
                .map(Ally::getAlly)
                .findFirst();
    }

    @Override
    public List<Player> findWards(Game game, Player player) {
        return allyRepo.findAll(game, player).stream()
                .map(Ally::getAlly)
                .filter(ally -> findGuardian(game, ally).stream().anyMatch(player::equals))
                .collect(Collectors.toList());
    }
}
