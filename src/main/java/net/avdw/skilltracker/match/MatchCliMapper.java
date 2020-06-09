package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import de.gesundkrank.jskills.Rating;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerService;
import net.avdw.skilltracker.player.PlayerTable;

import java.util.ArrayList;
import java.util.List;

public class MatchCliMapper {
    private final PlayerService playerService;
    private final MatchService matchService;
    private final MatchMapper matchMapper;

    @Inject
    MatchCliMapper(final PlayerService playerService, final MatchService matchService, final MatchMapper matchMapper) {
        this.playerService = playerService;
        this.matchService = matchService;
        this.matchMapper = matchMapper;
    }

    public List<ITeam> map(final GameTable gameTable, final List<String> teams) {
        List<ITeam> teamList = new ArrayList<>();
        for (String team : teams) {
            MatchTeam sessionTeam = new MatchTeam();
            String[] nameArray = team.split(",");
            for (String name : nameArray) {
                PlayerTable playerTable = playerService.createOrRetrievePlayer(name);
                MatchTable sessionTable = matchService.retrieveLatestPlayerSessionForGame(gameTable, playerTable);
                if (sessionTable != null) {
                    sessionTeam.put(playerTable, matchMapper.map(sessionTable));
                } else {
                    sessionTeam.put(playerTable,
                            new Rating(gameTable.getInitialMean().doubleValue(), gameTable.getInitialStandardDeviation().doubleValue()));
                }
            }
            teamList.add(sessionTeam);
        }
        return teamList;
    }
}
