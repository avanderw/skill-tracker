package net.avdw.skilltracker.match;

import lombok.Data;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class TeamData {
    private final Set<OrmLitePlayer> ormLitePlayerSet = new LinkedHashSet<>();

    public void add(final OrmLitePlayer name) {
        ormLitePlayerSet.add(name);
    }
}
