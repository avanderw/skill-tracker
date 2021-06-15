package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.cli.game.model.GameMatchModel;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Match;
import net.avdw.skilltracker.domain.Player;

import java.time.LocalDate;
import java.util.List;

public interface MatchQuery {
    Integer totalMatches(Game game, Player player);

    Integer totalMatches(Player player);

    LocalDate lastPlayedDate(Player player);

    List<Match> findLastBy(Game game, Long limit);
}
