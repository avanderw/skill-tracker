package net.avdw.skilltracker.cli.player.move;

import net.avdw.skilltracker.cli.converter.PlayerTypeConverter;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.RenamePlayerCmd;
import net.avdw.skilltracker.port.in.query.PlayerQuery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;

@Command(name = "mv", description = "Change name / combine players.", mixinStandardHelpOptions = true)
public class MovePlayerCommand implements Runnable {
    @Spec private CommandSpec spec;

    @Parameters(description = "Name to change from", arity = "1", index = "0", converter = PlayerTypeConverter.class)
    private Player from;
    @Parameters(description = "Name to change to", arity = "1", index = "1")
    private String to;

    @Inject private PlayerQuery playerQuery;
    @Inject private RenamePlayerCmd renamePlayerCmd;
    @Inject private MovePlayerView movePlayerView;

    @Override
    public void run() {
        boolean isMerge = playerQuery.findByName(to).isPresent();
        boolean isSame = from.getName().equals(to);

        renamePlayerCmd.rename(from, to);
        spec.commandLine().getOut().println(movePlayerView.render(MovePlayerModel.builder()
                .from(from.getName())
                .to(to)
                .same(isSame)
                .merge(isMerge)
                .build()));
    }
}
