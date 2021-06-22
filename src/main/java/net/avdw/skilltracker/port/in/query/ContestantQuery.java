package net.avdw.skilltracker.port.in.query;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;

import java.util.List;

public interface ContestantQuery {
    List<Contestant> topContestantsBySkill(Game game, Long limit);

    Contestant findContestant(Game game, Player player);

    Game mostPlayedGame(Player player);
}
