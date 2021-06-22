package net.avdw.skilltracker.app.statistic;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.statistic.LastPlayedStatistic;
import net.avdw.skilltracker.port.out.PlayRepo;

import javax.inject.Inject;
import java.time.LocalDate;

public class LastPlayedService implements LastPlayedStatistic {
    private final PlayRepo playRepo;

    @Inject
    public LastPlayedService(PlayRepo playRepo) {
        this.playRepo = playRepo;
    }

    @Override
    public Game lookupLastGameFor(Player player) {
        return playRepo.lookupLastPlayFor(player).getGame();
    }

    @Override
    public LocalDate lookupLastDateFor(Player player) {
        return playRepo.lookupLastPlayFor(player).getDate();
    }

    @Override
    public LocalDate lookupLastDateFor(Game game, Player player) {
        return playRepo.lookupLastPlayFor(game, player).getDate();
    }
}
