package net.avdw.skilltracker.session;

import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import de.gesundkrank.jskills.Rating;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.game.GameTable;
import net.avdw.skilltracker.player.PlayerService;
import net.avdw.skilltracker.player.PlayerTable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Command(name = "create", description = "Some fancy description", mixinStandardHelpOptions = true)
class CreateSessionCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Parameters()
    private String[] teams; // player,player,player

    @Option(names = "--ranks", split = ",", required = true)
    private int[] ranks;

    @Option(names = "--game", required = true)
    private String game;

    @Inject
    @Session
    private ResourceBundle bundle;
    @Inject
    private PlayerService playerService;
    @Inject
    private SessionService sessionService;
    @Inject
    private SessionMapper sessionMapper;
    @Inject
    private GameService gameService;

    @Override
    public void run() {
        if (ranks.length != teams.length) {
            spec.commandLine().getOut().println(bundle.getString(SessionBundleKey.TEAM_RANK_COUNT_MISMATCH));
        }

        GameTable gameTable = gameService.retrieveGame(game);
        List<ITeam> teamList = new ArrayList<>();
        for (String team : teams) {
            SessionTeam sessionTeam = new SessionTeam();
            String[] nameArray = team.split(",");
            for (String name : nameArray) {
                PlayerTable playerTable = playerService.createOrRetrievePlayer(name);
                SessionTable sessionTable = sessionService.retrieveLatestPlayerSessionForGame(gameTable, playerTable);
                if (sessionTable != null) {
                    sessionTeam.put(playerTable, sessionMapper.map(sessionTable));
                } else {
                    sessionTeam.put(playerTable,
                            new Rating(gameTable.getInitialMean().doubleValue(), gameTable.getInitialStandardDeviation().doubleValue()));
                }
            }
            teamList.add(sessionTeam);
        }

        sessionService.createSessionForGame(gameTable, teamList, ranks);
        spec.commandLine().getOut().println(bundle.getString(SessionBundleKey.CREATE_SUCCESS));
    }
}
