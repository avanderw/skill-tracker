package net.avdw.skilltracker.match;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.skilltracker.Templator;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Command(name = "rm", description = "Remove matches", mixinStandardHelpOptions = true)
class DeleteMatchCli implements Runnable {
    @Parameters(description = "IDs of the matches to delete", arity = "1..*")
    private List<String> idList;
    @Inject
    private MatchService matchService;
    @Spec
    private CommandSpec spec;
    @Inject
    @Match
    private Templator templator;
    private final Gson gson = new Gson();

    @Override
    public void run() {
        Logger.trace("Deleting matches {}", idList);

        spec.commandLine().getOut().println(templator.populate(MatchBundleKey.DELETE_COMMAND,
                gson.fromJson(String.format("{ids:'%s'}", idList), Map.class)));

        AtomicBoolean notDeleted = new AtomicBoolean(false);
        idList.forEach(id -> {
            if (matchService.deleteMatch(id)) {
                spec.commandLine().getOut().println(templator.populate(MatchBundleKey.DELETE_ENTRY_SUCCESS,
                        gson.fromJson(String.format("{id:'%s'}", id), Map.class)));
            } else {
                spec.commandLine().getOut().println(templator.populate(MatchBundleKey.DELETE_ENTRY_FAILURE,
                        gson.fromJson(String.format("{id:'%s'}", id), Map.class)));
                notDeleted.set(true);
            }
        });

        if (notDeleted.get()) {
            spec.commandLine().getOut().println(templator.populate(MatchBundleKey.DELETE_COMMAND_FAILURE));
        }
    }
}
