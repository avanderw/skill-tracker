package net.avdw.skilltracker.game;

import de.gesundkrank.jskills.GameInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    default GameInfo map(GameTable gameTable) {
        return new GameInfo(gameTable.getInitialMean().doubleValue(),
                gameTable.getInitialStandardDeviation().doubleValue(),
                gameTable.getBeta().doubleValue(),
                gameTable.getDynamicsFactor().doubleValue(),
                gameTable.getDrawProbability().doubleValue());
    }
}
