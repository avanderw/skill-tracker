package net.avdw.skilltracker.game;

import de.gesundkrank.jskills.GameInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);
    GameInfo DEFAULT_GAME_INFO = GameInfo.getDefaultGameInfo();

    default GameInfo map(GameTable gameTable) {
        return new GameInfo(DEFAULT_GAME_INFO.getInitialMean(),
                DEFAULT_GAME_INFO.getInitialStandardDeviation(),
                DEFAULT_GAME_INFO.getBeta(),
                DEFAULT_GAME_INFO.getDynamicsFactor(),
                gameTable.getDrawProbability().doubleValue());
    }
}
