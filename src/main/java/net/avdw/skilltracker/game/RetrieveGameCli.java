package net.avdw.skilltracker.game;

import com.google.inject.Inject;
import net.avdw.skilltracker.match.MatchService;
import net.avdw.skilltracker.match.MatchTable;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.Map;

@CommandLine.Command(name = "view", description = "View the details of a game", mixinStandardHelpOptions = true)
public class RetrieveGameCli implements Runnable {
    @Parameters(arity = "1")
    private String game;
    @Inject
    private GameService gameService;
    @Inject
    private MatchService matchService;
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        GameTable gameTable = gameService.retrieveGame(game);
        Map<String, List<MatchTable>> matchPlayerMap = matchService.retrieveAllMatchesForGame(gameTable);
        matchPlayerMap.forEach((key, matchTableList) -> {
            spec.commandLine().getOut().println(key);
        });
    }
}
