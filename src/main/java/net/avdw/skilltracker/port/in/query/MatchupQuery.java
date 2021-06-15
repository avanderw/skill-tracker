package net.avdw.skilltracker.port.in.query;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Matchup;
import net.avdw.skilltracker.domain.Player;

public interface MatchupQuery {
    Matchup findBy(Player player, Player opponent);
    Matchup findBy(Player player, Player opponent, Game game);
}
