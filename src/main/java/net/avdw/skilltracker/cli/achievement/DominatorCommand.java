package net.avdw.skilltracker.cli.achievement;

import net.avdw.skilltracker.cli.achievement.view.AchievementModel;
import net.avdw.skilltracker.cli.achievement.view.AchievementView;
import net.avdw.skilltracker.port.in.query.stat.DominatorQuery;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.inject.Inject;

@Command(name = "dominator", description = "Highly skilled player.", mixinStandardHelpOptions = true)
public class DominatorCommand implements Runnable {
    @CommandLine.Spec private CommandLine.Model.CommandSpec spec;

    @Inject private AchievementView achievementView;
    @Inject private DominatorQuery dominatorQuery;

    @Override
    public void run() {
        spec.commandLine().getOut().println(achievementView.render(AchievementModel.builder()
                .title(dominatorQuery.getTitle())
                .description(dominatorQuery.getDescription())
                .build()));
    }
}
