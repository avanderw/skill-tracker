package net.avdw.skilltracker.cli.achievement;

import net.avdw.skilltracker.cli.achievement.view.AchievementModel;
import net.avdw.skilltracker.cli.achievement.view.AchievementView;
import net.avdw.skilltracker.port.in.query.stat.GuardianQuery;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.inject.Inject;

@Command(name = "guardian", description = "Strong ally.", mixinStandardHelpOptions = true)
public class GuardianCommand implements Runnable {
    @CommandLine.Spec private CommandLine.Model.CommandSpec spec;

    @Inject private AchievementView achievementView;
    @Inject private GuardianQuery guardianQuery;

    @Override
    public void run() {
        spec.commandLine().getOut().println(achievementView.render(AchievementModel.builder()
                .title(guardianQuery.getTitle())
                .description(guardianQuery.getDescription())
                .build()));
    }
}
