package net.avdw.skilltracker.match;

import com.google.inject.Inject;
import de.gesundkrank.jskills.ITeam;
import net.avdw.skilltracker.game.GameService;
import net.avdw.skilltracker.game.GameTable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.ResourceBundle;

@Command(name = "add", description = "Add a match with an outcome", mixinStandardHelpOptions = true)
class CreateMatchCli implements Runnable {
    @Inject
    @Match
    private ResourceBundle bundle;
    @Option(names = {"-g", "--game"}, required = true)
    private String game;
    @Inject
    private GameMatchTeamBuilder gameMatchTeamBuilder;
    @Inject
    private GameService gameService;
    @Inject
    private MatchService matchService;
    @Option(names = {"-r", "--ranks"}, split = ",", required = true)
    private int[] ranks;
    @Spec
    private CommandSpec spec;
    @Parameters(description = "Teams in the match; team=<player1,player2> (no spaces)", arity = "2..*")
    private List<String> teams;

    @Override
    public void run() {
        if (ranks.length != teams.size()) {
            spec.commandLine().getOut().println(bundle.getString(MatchBundleKey.TEAM_RANK_COUNT_MISMATCH));
        }

        GameTable gameTable = gameService.retrieveGame(game);
        List<ITeam> teamList = gameMatchTeamBuilder.build(gameTable, teams);
        matchService.createMatchForGame(gameTable, teamList, ranks);
        spec.commandLine().getOut().println(bundle.getString(MatchBundleKey.CREATE_SUCCESS));
    }
}
