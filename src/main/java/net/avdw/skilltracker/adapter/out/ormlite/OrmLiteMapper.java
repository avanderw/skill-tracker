package net.avdw.skilltracker.adapter.out.ormlite;

import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface OrmLiteMapper {


    @Mapping(target = "name", source = "gameName")
    Game toGame(PlayEntity playEntity);

    @Mapping(target = "name", source = "playerName")
    Player toPlayer(PlayEntity playEntity);
}
