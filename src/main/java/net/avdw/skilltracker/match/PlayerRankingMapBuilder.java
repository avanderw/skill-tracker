package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.game.GameTable;
import org.tinylog.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerRankingMapBuilder {
    private final MatchMapper matchMapper;
    private final MatchService matchService;

    @Inject
    PlayerRankingMapBuilder(final MatchService matchService, final MatchMapper matchMapper) {
        this.matchService = matchService;
        this.matchMapper = matchMapper;
    }

    public List<ITeam> build(final GameTable gameTable, final MatchData matchData) {
        Logger.trace("Build player ranking GAME={}, MATCH={}", gameTable, matchData);
        return matchData.getTeamDataSet().stream().map(teamData -> {
            PlayerRankingMap playerRankingMap = new PlayerRankingMap();
            teamData.getPlayerTableSet().forEach(playerTable ->
                    playerRankingMap.put(playerTable, matchMapper.toRating(matchService.retrieveLatestPlayerMatchForGame(gameTable, playerTable))));
            return playerRankingMap;
        }).collect(Collectors.toList());
    }
}
