package net.avdw.skilltracker.app.statistic;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.PriorityObject;
import net.avdw.skilltracker.port.in.query.statistic.HIndexStatistic;
import net.avdw.skilltracker.port.out.PlayRepo;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class HIndexService implements HIndexStatistic {
    private final PlayRepo playRepo;

    @Inject
    public HIndexService(PlayRepo playRepo) {
        this.playRepo = playRepo;
    }

    @Override
    public Integer getIndex(Player player) {
        List<Game> games = playRepo.findAllGamesFor(player);
        Queue<PriorityObject<Game>> queue = new PriorityQueue<>(
                Comparator.comparing((PriorityObject<Game> po) -> po.getPriority().intValue()).reversed());
        games.stream().map(game ->
                PriorityObject.<Game>builder()
                        .object(game)
                        .priority(playRepo.lookupPlayCountFor(game, player))
                        .build())
                .forEach(queue::add);

        int hIndex = 0;
        while (!queue.isEmpty()) {
            if (queue.poll().getPriority().intValue() <= hIndex) {
                break;
            }
            hIndex++;
        }

        return hIndex;
    }
}
