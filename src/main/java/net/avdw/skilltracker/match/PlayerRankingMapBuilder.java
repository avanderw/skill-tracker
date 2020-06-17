package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerService;
import net.avdw.skilltracker.player.PlayerTable;

import java.util.List;
import java.util.stream.Collectors;

class PlayerRankingMapBuilder {
    private final PlayerService playerService;
    private final MatchService matchService;
    private final MatchMapper matchMapper;

    @Inject
    PlayerRankingMapBuilder(final PlayerService playerService, final MatchService matchService, final MatchMapper matchMapper) {
        this.playerService = playerService;
        this.matchService = matchService;
        this.matchMapper = matchMapper;
    }

    @Deprecated(forRemoval = true)
    public List<ITeam> build(final GameTable gameTable, final List<String> teams) {
        return teams.stream().map(team -> buildPlayerRankingMap(gameTable, team)).collect(Collectors.toList());
    }

    private ITeam buildPlayerRankingMap(final GameTable gameTable, final String team) {
        PlayerRankingMap playerRankingMap = new PlayerRankingMap();
        String[] nameArray = team.split(",");
        for (final String name : nameArray) {
            PlayerTable playerTable = playerService.createOrRetrievePlayer(name);
            MatchTable matchTable = matchService.retrieveLatestPlayerMatchForGame(gameTable, playerTable);
            playerRankingMap.put(playerTable, matchMapper.map(matchTable));
        }
        return playerRankingMap;
    }

    public List<ITeam> build(final GameTable gameTable, final MatchData matchData) {
        return matchData.getTeamDataSet().stream().map(teamData -> {
            PlayerRankingMap playerRankingMap = new PlayerRankingMap();
            teamData.getPlayerTableSet().forEach(playerTable -> {
                playerRankingMap.put(playerTable, matchMapper.map(matchService.retrieveLatestPlayerMatchForGame(gameTable, playerTable)));
            });
            return playerRankingMap;
        }).collect(Collectors.toList());
    }
}
