package net.avdw.skilltracker.cli.converter;

import net.avdw.skilltracker.app.exception.PlayerNotFoundException;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.PlayerQuery;
import picocli.CommandLine;

import javax.inject.Inject;

public class PlayerTypeConverter implements CommandLine.ITypeConverter<Player> {
    private final PlayerQuery playerQuery;

    @Inject
    public PlayerTypeConverter(PlayerQuery playerQuery) {
        this.playerQuery = playerQuery;
    }

    @Override
    public Player convert(String name) {
        return playerQuery.findByName(name).orElseThrow(PlayerNotFoundException::new);
    }
}
