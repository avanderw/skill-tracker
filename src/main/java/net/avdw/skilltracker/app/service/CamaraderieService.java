package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Ally;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.stat.CamaraderieQuery;
import net.avdw.skilltracker.port.in.query.stat.ComradeQuery;
import net.avdw.skilltracker.port.out.AllyRepo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CamaraderieService implements CamaraderieQuery {
    private final AllyRepo allyRepo;
    private final ComradeQuery comradeQuery;

    @Inject
    public CamaraderieService(AllyRepo allyRepo, ComradeQuery comradeQuery) {
        this.allyRepo = allyRepo;
        this.comradeQuery = comradeQuery;
    }

    @Override
    public List<Player> findAll(Player player) {
        return allyRepo.findAll(player).stream()
                .map(Ally::getAlly)
                .filter(ally -> comradeQuery.find(ally).stream().anyMatch(player::equals))
                .sorted(Comparator.comparing(Player::getName))
                .collect(Collectors.toList());

    }

    @Override
    public List<Player> findAll(Game game, Player player) {
        return allyRepo.findAll(game, player).stream()
                .map(Ally::getAlly)
                .filter(ally -> comradeQuery.find(game, ally).stream().anyMatch(player::equals))
                .sorted(Comparator.comparing(Player::getName))
                .collect(Collectors.toList());
    }
}
