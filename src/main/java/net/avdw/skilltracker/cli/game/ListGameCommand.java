package net.avdw.skilltracker.cli.game;

import net.avdw.skilltracker.cli.game.view.GameListModel;
import net.avdw.skilltracker.cli.game.view.GameListView;
import net.avdw.skilltracker.port.in.query.GameQuery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;

@Command(name = "ls", description = "List all games.", mixinStandardHelpOptions = true)
public class ListGameCommand implements Runnable {

    @Spec private CommandSpec spec;
    @Parameters(arity = "0..1") private String search;

    @Inject private GameListView gameListView;
    @Inject private GameQuery gameQuery;

    @Override
    public void run() {
        if (search == null) {
            spec.commandLine().getOut().println(gameListView.render(GameListModel.builder()
                    .games(gameQuery.findAll())
                    .build()));
        } else {
            spec.commandLine().getOut().println(gameListView.render(GameListModel.builder()
                    .games(gameQuery.findLike(search))
                    .build()));
        }
    }
}
