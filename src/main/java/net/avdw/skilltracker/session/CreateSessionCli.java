package net.avdw.skilltracker.session;

import com.google.inject.Inject;
import org.tinylog.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.ResourceBundle;

@Command(name = "create", description = "Some fancy description", mixinStandardHelpOptions = true)
class CreateSessionCli implements Runnable {
    @Spec
    CommandSpec spec;

    @Parameters()
    String[] teams; // player,player,player

    @Option(names = "--ranks", split = ",", required = true)
    int[] ranks;

    @Option(names = "--game", required = true)
    String game;

    @Inject
    ResourceBundle bundle;

    @Override
    public void run() {
        if (ranks.length != teams.length) {
            spec.commandLine().getOut().println(bundle.getString(SessionBundleKey.TEAM_RANK_COUNT_MISMATCH));
        }

        spec.commandLine().getOut().println(bundle.getString(SessionBundleKey.CREATE_SUCCESS));
    }
}
