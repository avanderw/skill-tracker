package net.avdw.skilltracker.maintenance;

import com.google.inject.Guice;
import com.google.inject.Inject;
import net.avdw.skilltracker.MainModule;
import net.avdw.skilltracker.adapter.out.ormlite.PlayerDbAdapter;

public class CleanPlayer implements Runnable {
    private final PlayerDbAdapter playerDbAdapter;

    public static void main(final String[] args) {
        Guice.createInjector(new MainModule()).getInstance(CleanPlayer.class).run();
    }

    @Inject
    CleanPlayer(final PlayerDbAdapter playerDbAdapter) {
        this.playerDbAdapter = playerDbAdapter;
    }

    @Override
    public void run() {
        playerDbAdapter.removePlayersWithNoMatches();
    }
}
