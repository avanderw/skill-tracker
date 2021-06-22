package net.avdw.skilltracker.adapter.out.ormlite;

import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Play;
import net.avdw.skilltracker.domain.Player;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface DbMapper {


    @Mapping(target = "name", source = "gameName")
    Game toGame(PlayEntity entity);

    @Mapping(target = "name", source = "playerName")
    Player toPlayer(PlayEntity entity);

    @Mapping(target = "player", source = "playerName")
    @Mapping(target = "game", source = "gameName")
    @Mapping(target = "date", source = "playDate")
    Play toPlay(PlayEntity entity);

    @Mapping(target = "name", source = "name")
    Game toGame(String name);

    @Mapping(target = "name", source = "name")
    Player toPlayer(String name);
}
