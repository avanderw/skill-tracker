package net.avdw.skilltracker.cli.achievement.view;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class AchievementModel {
    String title;
    String description;
}
