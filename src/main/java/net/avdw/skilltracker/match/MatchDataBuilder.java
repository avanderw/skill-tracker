package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import net.avdw.skilltracker.player.PlayerService;
import org.tinylog.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchDataBuilder {
    private PlayerService playerService;

    @Inject
    MatchDataBuilder(final PlayerService playerService) {
        this.playerService = playerService;
    }

    public MatchData buildFromString(final List<String> teams) {
        MatchData matchData = new MatchData();
        if (teams.size() == 1) {
            for (final String player : teams.get(0).split(",")) {
                TeamData teamData = new TeamData();
                teamData.add(playerService.createOrRetrievePlayer(player));
                matchData.add(teamData);
            }
        } else {
            teams.forEach(team -> {
                TeamData teamData = new TeamData();
                for (final String player : team.split(",")) {
                    teamData.add(playerService.createOrRetrievePlayer(player));
                }
                matchData.add(teamData);
            });
        }
        return matchData;
    }

    public MatchData buildFromMatchTable(final List<MatchTable> sessionMatchTableList) {
        MatchData matchData = new MatchData();
        Map<Integer, List<MatchTable>> groupByTeam = sessionMatchTableList.stream().collect(Collectors.groupingBy(MatchTable::getTeam));
        groupByTeam.forEach((teamId, matchList)->{
            TeamData teamData = new TeamData();
            matchList.forEach(matchTable -> {
                teamData.add(matchTable.getPlayerTable());
            });
            matchData.add(teamData);
        });
        return matchData;
    }
}
