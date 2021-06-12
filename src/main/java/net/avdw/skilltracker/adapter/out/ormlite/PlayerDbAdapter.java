package net.avdw.skilltracker.adapter.out.ormlite;

import com.google.inject.Inject;
import lombok.SneakyThrows;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLitePlayer;

public class PlayerDbAdapter {

    @Inject
    PlayerDbAdapter() {

    }

    @SneakyThrows
    public void changeName(final OrmLitePlayer fromPlayer, final String toName) {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    public void removePlayersWithNoMatches() {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    public OrmLitePlayer retrievePlayer(final String name) {
        return new OrmLitePlayer(name);
    }

}
