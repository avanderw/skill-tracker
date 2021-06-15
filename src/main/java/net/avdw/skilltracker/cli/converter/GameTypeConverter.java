package net.avdw.skilltracker.cli.converter;

import net.avdw.skilltracker.app.exception.GameNotFoundException;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.port.in.query.GameQuery;
import picocli.CommandLine;

import javax.inject.Inject;

public class GameTypeConverter implements CommandLine.ITypeConverter<Game> {
    private final GameQuery gameQuery;

    @Inject
    public GameTypeConverter(GameQuery gameQuery) {
        this.gameQuery = gameQuery;
    }

    @Override
    public Game convert(String name) {
        return gameQuery.findAll(name).orElseThrow(GameNotFoundException::new);
    }
}
