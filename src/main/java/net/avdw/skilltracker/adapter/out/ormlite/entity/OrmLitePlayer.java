package net.avdw.skilltracker.adapter.out.ormlite.entity;

import de.gesundkrank.jskills.IPlayer;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data @Builder
public class OrmLitePlayer implements IPlayer {

    private final String name;
    private final String uuid = UUID.randomUUID().toString();

    public OrmLitePlayer(final String name) {
        this.name = name;
    }
}
