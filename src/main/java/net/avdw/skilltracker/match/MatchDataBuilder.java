package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchDataBuilder {

    @Inject
    MatchDataBuilder() {

    }

    public MatchData buildFromMatchTable(final List<PlayEntity> sessionOrmLiteMatchList) {
        MatchData matchData = new MatchData();
        Map<Integer, List<PlayEntity>> groupByTeam = sessionOrmLiteMatchList.stream().collect(Collectors.groupingBy(PlayEntity::getPlayerTeam));
        groupByTeam.forEach((teamId, matchList) -> {
            TeamData teamData = new TeamData();
            matchList.forEach(matchTable -> {
                teamData.add(new OrmLitePlayer(matchTable.getPlayerName()));
            });
            matchData.add(teamData);
        });
        return matchData;
    }

    public MatchData buildFromString(final List<String> teams) {
        MatchData matchData = new MatchData();
        if (teams.size() == 1) {
            for (final String player : teams.get(0).split(",")) {
                TeamData teamData = new TeamData();
                teamData.add(new OrmLitePlayer(player));
                matchData.add(teamData);
            }
        } else {
            teams.forEach(team -> {
                TeamData teamData = new TeamData();
                for (final String player : team.split(",")) {
                    teamData.add(new OrmLitePlayer(player));
                }
                matchData.add(teamData);
            });
        }
        return matchData;
    }
}
