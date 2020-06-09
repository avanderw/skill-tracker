package net.avdw.skilltracker.match;

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
class CreateMatchCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Parameters()
    private List<String> teams; // player,player,player

    @Option(names = {"-g", "--game"}, required = true)
    private String game;

    @Option(names = {"-r", "--ranks"}, split = ",", required = true)
    private int[] ranks;

    @Inject
    @Match
    private ResourceBundle bundle;
    @Inject
    private PlayerService playerService;
    @Inject
    private MatchService matchService;
    @Inject
    private MatchMapper matchMapper;
    @Inject
    private MatchCliMapper matchCliMapper;
    @Inject
    private GameService gameService;

    @Override
    public void run() {
        if (ranks.length != teams.size()) {
            spec.commandLine().getOut().println(bundle.getString(MatchBundleKey.TEAM_RANK_COUNT_MISMATCH));
        }

        GameTable gameTable = gameService.retrieveGame(game);
        List<ITeam> teamList = matchCliMapper.map(gameTable, teams);
        matchService.createSessionForGame(gameTable, teamList, ranks);
        spec.commandLine().getOut().println(bundle.getString(MatchBundleKey.CREATE_SUCCESS));
    }
}
