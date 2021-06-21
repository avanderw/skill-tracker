package net.avdw.skilltracker.cli.achievement;

import net.avdw.skilltracker.cli.achievement.view.AchievementModel;
import net.avdw.skilltracker.cli.achievement.view.AchievementView;
import net.avdw.skilltracker.port.in.query.stat.NemesisQuery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import javax.inject.Inject;

@Command(name = "nemesis", description = "Strong opponent.", mixinStandardHelpOptions = true)
public class NemesisCommand implements Runnable {
    @Spec private CommandSpec spec;

    @Inject private AchievementView achievementView;
    @Inject private NemesisQuery nemesisQuery;

    @Override
    public void run() {
        spec.commandLine().getOut().println(achievementView.render(AchievementModel.builder()
                .title(nemesisQuery.getTitle())
                .description(nemesisQuery.getDescription())
                .build()));
    }
}
