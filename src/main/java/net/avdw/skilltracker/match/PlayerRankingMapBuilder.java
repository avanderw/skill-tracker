package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;

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

    public List<ITeam> build(final OrmLiteGame ormLiteGame, final MatchData matchData) {
        return matchData.getTeamDataSet().stream().map(teamData -> {
            PlayerRankingMap playerRankingMap = new PlayerRankingMap();
            teamData.getOrmLitePlayerSet().forEach(playerTable ->
                    playerRankingMap.put(playerTable, matchMapper.toRating(matchService.retrieveLastPlayerMatchForGame(ormLiteGame, playerTable))));
            return playerRankingMap;
        }).collect(Collectors.toList());
    }
}
