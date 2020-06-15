package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import de.gesundkrank.jskills.GameInfo;
import de.gesundkrank.jskills.ITeam;
import de.gesundkrank.jskills.Rating;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerService;
import net.avdw.skilltracker.player.PlayerTable;

import java.util.List;
import java.util.stream.Collectors;

class GameMatchTeamBuilder {
    private final PlayerService playerService;
    private final MatchService matchService;
    private final MatchMapper matchMapper;
    private final GameInfo defaultGameInfo;

    @Inject
    GameMatchTeamBuilder(final PlayerService playerService, final MatchService matchService, final MatchMapper matchMapper, final GameInfo defaultGameInfo) {
        this.playerService = playerService;
        this.matchService = matchService;
        this.matchMapper = matchMapper;
        this.defaultGameInfo = defaultGameInfo;
    }

    public List<ITeam> build(final GameTable gameTable, final List<String> teams) {
        return teams.stream().map(team -> buildTeam(gameTable, team)).collect(Collectors.toList());
    }

    private ITeam buildTeam(final GameTable gameTable, final String team) {
        MatchTeam matchTeam = new MatchTeam();
        String[] nameArray = team.split(",");
        for (final String name : nameArray) {
            PlayerTable playerTable = playerService.createOrRetrievePlayer(name);
            MatchTable sessionTable = matchService.retrieveLatestPlayerSessionForGame(gameTable, playerTable);
            if (sessionTable != null) {
                matchTeam.put(playerTable, matchMapper.map(sessionTable));
            } else {
                matchTeam.put(playerTable,
                        new Rating(defaultGameInfo.getInitialMean(), defaultGameInfo.getInitialStandardDeviation()));
            }
        }
        return matchTeam;
    }
}
