package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.GameQuery;
import net.avdw.skilltracker.port.out.GameRepo;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

public class GameService implements GameQuery {

    private final GameRepo gameRepo;

    @Inject
    public GameService(GameRepo gameRepo) {
        this.gameRepo = gameRepo;
    }

    @Override
    public Optional<Game> findAll(String name) {
        return gameRepo.findBy(name);
    }

    @Override
    public Set<Game> findAll(Player player) {
        return gameRepo.findBy(player);
    }

    @Override
    public Integer totalGames(Player player) {
        return gameRepo.totalGames(player);
    }

    @Override
    public Game lastPlayed(Player player) {
        return gameRepo.lastPlayed(player);
    }
}
