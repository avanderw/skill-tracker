package net.avdw.skilltracker.cli.achievement;

import net.avdw.skilltracker.cli.achievement.view.AchievementModel;
import net.avdw.skilltracker.cli.achievement.view.AchievementView;
import net.avdw.skilltracker.port.in.query.stat.EnthusiastQuery;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.inject.Inject;

@Command(name = "enthusiast", description = "Interested player.", mixinStandardHelpOptions = true)
public class EnthusiastCommand implements Runnable {
    @CommandLine.Spec private CommandLine.Model.CommandSpec spec;

    @Inject private AchievementView achievementView;
    @Inject private EnthusiastQuery enthusiastQuery;

    @Override
    public void run() {
        spec.commandLine().getOut().println(achievementView.render(AchievementModel.builder()
                .title(enthusiastQuery.getTitle())
                .description(enthusiastQuery.getDescription())
                .build()));
    }
}
