package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.PlayerQuery;
import net.avdw.skilltracker.port.out.PlayerRepo;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

public class PlayerService implements PlayerQuery {
    private final PlayerRepo playerRepo;

    @Inject
    public PlayerService(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
    }

    @Override
    public Optional<Player> findByName(String name) {
        return playerRepo.findBy(name);
    }

    @Override
    public Set<Player> findAll() {
        return playerRepo.findAll();
    }
}
