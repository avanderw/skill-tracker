package net.avdw.skilltracker.session;

import de.gesundkrank.jskills.Rating;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerTable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper(imports = Date.class)
public interface SessionMapper {
    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);

    default Rating map(SessionTable sessionTable) {
        return new Rating(sessionTable.getMean().doubleValue(), sessionTable.getStandardDeviation().doubleValue());
    };

    @Mappings({
            @Mapping(target = SessionTable.SESSION_ID),
            @Mapping(target = SessionTable.GAME_FK, source = "gameTable.pk"),
            @Mapping(target = SessionTable.PLAYER_FK, source = "playerTable.pk"),
            @Mapping(target = SessionTable.TEAM),
            @Mapping(target = SessionTable.RANK),
            @Mapping(target = SessionTable.PLAY_DATE, expression = "java(new Date())"),
            @Mapping(target = SessionTable.MEAN),
            @Mapping(target = SessionTable.STANDARD_DEVIATION),
    })
    SessionTable map(GameTable gameTable, PlayerTable playerTable, Rating rating);
}
