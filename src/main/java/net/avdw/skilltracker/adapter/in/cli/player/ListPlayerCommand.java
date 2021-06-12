package net.avdw.skilltracker.adapter.in.cli.player;

import net.avdw.skilltracker.adapter.in.cli.player.model.PlayerListDetailModel;
import net.avdw.skilltracker.adapter.in.cli.player.model.PlayerListModel;
import net.avdw.skilltracker.adapter.in.cli.player.view.PlayerListDetailView;
import net.avdw.skilltracker.adapter.in.cli.player.view.PlayerListView;
import net.avdw.skilltracker.port.in.GameQuery;
import net.avdw.skilltracker.port.in.MatchQuery;
import net.avdw.skilltracker.port.in.PlayerQuery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.util.stream.Collectors;

@Command(name = "ls", description = "List all players", mixinStandardHelpOptions = true)
public class ListPlayerCommand implements Runnable {
    @Spec private CommandSpec spec;

    @Option(names = "-l") boolean isLong;

    @Inject private PlayerQuery playerQuery;
    @Inject private GameQuery gameQuery;
    @Inject private MatchQuery matchQuery;
    @Inject private PlayerListView playerListView;
    @Inject private PlayerListDetailView playerListDetailView;

    @Override
    public void run() {
        if (isLong) {
            spec.commandLine().getOut().println(playerListDetailView.render(PlayerListDetailModel.builder()
                    .players(playerQuery.findAll().stream().map(p -> PlayerListDetailModel.ListItem.builder()
                            .player(p)
                            .lastPlayed(matchQuery.lastPlayedDate(p))
                            .totalGames(gameQuery.totalGames(p))
                            .totalMatches(matchQuery.totalMatches(p))
                            .build()).collect(Collectors.toList()))
                    .build()));
        } else {
            spec.commandLine().getOut().println(playerListView.render(PlayerListModel.builder()
                    .players(playerQuery.findAll())
                    .build()));
        }
    }
}
