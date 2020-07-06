package net.avdw.skilltracker.maintenance;

import com.google.inject.Guice;
import com.google.inject.Inject;
import net.avdw.skilltracker.MainModule;
import net.avdw.skilltracker.player.PlayerService;

public class CleanPlayer implements Runnable {
    private final PlayerService playerService;

    public static void main(final String[] args) {
        Guice.createInjector(new MainModule()).getInstance(CleanPlayer.class).run();
    }

    @Inject
    CleanPlayer(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public void run() {
        playerService.removePlayersWithNoMatches();
    }
}
