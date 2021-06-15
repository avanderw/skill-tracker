package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;

public interface ContestantRepo {
    Contestant mostWinsForGame(Game game);
    List<Contestant> topContestantsBySkill(Game game, Long limit);
    Long winCount(Game game, Player player);
}
