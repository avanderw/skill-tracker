package net.avdw.skilltracker.match;

import de.gesundkrank.jskills.Rating;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerTable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper(imports = Date.class)
public interface MatchMapper {
    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

    default Rating map(MatchTable sessionTable) {
        return new Rating(sessionTable.getMean().doubleValue(), sessionTable.getStandardDeviation().doubleValue());
    };

    @Mappings({
            @Mapping(target = MatchTable.SESSION_ID),
            @Mapping(target = MatchTable.GAME_FK, source = "gameTable.pk"),
            @Mapping(target = MatchTable.PLAYER_FK, source = "playerTable.pk"),
            @Mapping(target = MatchTable.TEAM),
            @Mapping(target = MatchTable.RANK),
            @Mapping(target = MatchTable.PLAY_DATE, expression = "java(new Date())"),
            @Mapping(target = MatchTable.MEAN),
            @Mapping(target = MatchTable.STANDARD_DEVIATION),
    })
    MatchTable map(GameTable gameTable, PlayerTable playerTable, Rating rating);
}
