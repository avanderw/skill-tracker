package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import net.avdw.skilltracker.player.PlayerService;

import java.util.List;

public class MatchDataBuilder {
    private PlayerService playerService;

    @Inject
    MatchDataBuilder(final PlayerService playerService) {
        this.playerService = playerService;
    }

    public MatchData build(final List<String> teams) {
        MatchData matchData = new MatchData();
        if (teams.size() == 1) {
            for (final String player : teams.get(0).split(";")) {
                TeamData teamData = new TeamData();
                teamData.add(playerService.createOrRetrievePlayer(player));
                matchData.add(teamData);
            }
        } else {
            teams.forEach(team -> {
                TeamData teamData = new TeamData();
                for (final String player : team.split(";")) {
                    teamData.add(playerService.createOrRetrievePlayer(player));
                }
                matchData.add(teamData);
            });
        }
        return matchData;
    }
}
