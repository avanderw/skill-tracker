package net.avdw.skilltracker.cli.achievement.view;

public class AchievementView {
    private static final String INFO = "" +
            "Achievement: %s%n%n" +
            "%s%n";

    public String render(AchievementModel model) {
        return String.format(INFO, model.getTitle(), model.getDescription());
    }
}
