package net.avdw.skilltracker.game;

import de.gesundkrank.jskills.GameInfo;
import de.gesundkrank.jskills.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);
    GameInfo DEFAULT_GAME_INFO = GameInfo.getDefaultGameInfo();

    default GameInfo toGameInfo(final GameTable gameTable) {
        return new GameInfo(DEFAULT_GAME_INFO.getInitialMean(),
                DEFAULT_GAME_INFO.getInitialStandardDeviation(),
                DEFAULT_GAME_INFO.getBeta(),
                DEFAULT_GAME_INFO.getDynamicsFactor(),
                gameTable.getDrawProbability().doubleValue());
    }

    default Rating toRating(final GameTable gameTable) {
        GameInfo gameInfo = toGameInfo(gameTable);
        return new Rating(gameInfo.getInitialMean(), gameInfo.getInitialStandardDeviation());
    }
}
