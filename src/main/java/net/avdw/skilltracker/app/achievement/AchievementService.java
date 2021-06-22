package net.avdw.skilltracker.app.achievement;

import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.achievement.AllAchievements;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class AchievementService implements AllAchievements {

    @Inject
    public AchievementService() {
    }

    @Override
    public List<KeyValue> forPlayer(Player player) {
        List<KeyValue> keyValues = new ArrayList<>();


        return keyValues;
    }
}
