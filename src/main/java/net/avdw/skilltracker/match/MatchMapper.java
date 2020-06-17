package net.avdw.skilltracker.match;

import de.gesundkrank.jskills.GameInfo;
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

    default Rating map(final MatchTable matchTable) {
        if (matchTable == null) {
            return GameInfo.getDefaultGameInfo().getDefaultRating();
        } else {
            return new Rating(matchTable.getMean().doubleValue(), matchTable.getStandardDeviation().doubleValue());
        }
    }

    @Mappings({
            @Mapping(target = MatchTable.PLAY_DATE, expression = "java(new Date())")
    })
    MatchTable map(GameTable gameTable, PlayerTable playerTable, Rating rating);
}
