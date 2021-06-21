package net.avdw.skilltracker.cli.achievement;

import net.avdw.skilltracker.cli.achievement.view.AchievementModel;
import net.avdw.skilltracker.cli.achievement.view.AchievementView;
import net.avdw.skilltracker.port.in.query.stat.ComradeQuery;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.inject.Inject;

@Command(name = "comrade", description = "Close friend.", mixinStandardHelpOptions = true)
public class ComradeCommand implements Runnable {
    @CommandLine.Spec private CommandLine.Model.CommandSpec spec;

    @Inject private AchievementView achievementView;
    @Inject private ComradeQuery comradeQuery;

    @Override
    public void run() {
        spec.commandLine().getOut().println(achievementView.render(AchievementModel.builder()
                .title(comradeQuery.getTitle())
                .description(comradeQuery.getDescription())
                .build()));
    }
}
