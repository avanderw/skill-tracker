package net.avdw.skilltracker.app.statistic;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.statistic.FirstPlayedStatistic;
import net.avdw.skilltracker.port.out.PlayRepo;

import javax.inject.Inject;
import java.time.LocalDate;

public class FirstPlayedService implements FirstPlayedStatistic {
    private final PlayRepo playRepo;

    @Inject
    public FirstPlayedService(PlayRepo playRepo) {
        this.playRepo = playRepo;
    }

    @Override
    public Game lookupFirstGameFor(Player player) {
        return playRepo.lookupFirstPlay(player).getGame();
    }

    @Override
    public LocalDate lookupFirstDateFor(Player player) {
        return playRepo.lookupFirstPlay(player).getDate();
    }
}
