package net.avdw.skilltracker.game;

import de.gesundkrank.jskills.GameInfo;
import de.gesundkrank.jskills.Rating;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);
    GameInfo DEFAULT_GAME_INFO = GameInfo.getDefaultGameInfo();

    default GameInfo toGameInfo(final OrmLiteGame ormLiteGame) {
        return new GameInfo(DEFAULT_GAME_INFO.getInitialMean(),
                DEFAULT_GAME_INFO.getInitialStandardDeviation(),
                DEFAULT_GAME_INFO.getBeta(),
                DEFAULT_GAME_INFO.getDynamicsFactor(),
                DEFAULT_GAME_INFO.getDrawProbability());
    }

    default Rating toRating(final OrmLiteGame ormLiteGame) {
        GameInfo gameInfo = toGameInfo(ormLiteGame);
        return new Rating(gameInfo.getInitialMean(), gameInfo.getInitialStandardDeviation());
    }
}
