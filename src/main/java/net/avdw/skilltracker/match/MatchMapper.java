package net.avdw.skilltracker.match;

import de.gesundkrank.jskills.GameInfo;
import de.gesundkrank.jskills.Rating;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteMatch;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MatchMapper {
    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

    OrmLiteMatch toMatchTable(OrmLiteMatch ormLiteMatch);

    @Mappings({
            @Mapping(target = OrmLiteMatch.PK, ignore = true),
            @Mapping(target = OrmLiteMatch.PLAY_DATE, ignore = true),
            @Mapping(target = "gameName", source = "game.name"),
            @Mapping(target = "playerMean", source = "rating.mean"),
            @Mapping(target = "playerName", source = "player.name"),
            @Mapping(target = "playerStdDev", source = "rating.standardDeviation"),
            @Mapping(target = "playerTeam", ignore = true),
            @Mapping(target = "sessionId", ignore = true),
            @Mapping(target = "teamRank", ignore = true)
    })
    PlayEntity toMatchTable(OrmLiteGame game, OrmLitePlayer player, Rating rating);

    default Rating toRating(final PlayEntity ormLiteMatch) {
        if (ormLiteMatch == null) {
            return GameInfo.getDefaultGameInfo().getDefaultRating();
        } else {
            return new Rating(ormLiteMatch.getPlayerMean().doubleValue(), ormLiteMatch.getPlayerStdDev().doubleValue());
        }
    }

    @Mapping(target = "team", source = "playerTeam")
    @Mapping(target = "standardDeviation", source = "playerStdDev")
    @Mapping(target = "rank", source = "teamRank")
    @Mapping(target = "player.name", source = "playerName")
    @Mapping(target = "mean", source = "playerMean")
    @Mapping(target = "game.name", source = "gameName")
    OrmLiteMatch map(PlayEntity playEntity);
    @InheritInverseConfiguration
    PlayEntity map(OrmLiteMatch collapseMatchTable);
}
