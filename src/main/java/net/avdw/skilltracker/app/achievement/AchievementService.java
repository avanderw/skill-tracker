package net.avdw.skilltracker.app.achievement;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.achievement.AllAchievements;
import net.avdw.skilltracker.port.in.query.achievement.GuardianAchievement;
import net.avdw.skilltracker.port.in.query.achievement.NemesisAchievement;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AchievementService implements AllAchievements {

    private final GuardianAchievement guardianAchievement;
    private final NemesisAchievement nemesisAchievement;

    @Inject
    public AchievementService(GuardianAchievement guardianAchievement, NemesisAchievement nemesisAchievement) {
        this.guardianAchievement = guardianAchievement;
        this.nemesisAchievement = nemesisAchievement;
    }

    @Override
    public List<KeyValue> findFor(Player player) {
        List<KeyValue> keyValues = new ArrayList<>();


        return keyValues;
    }

    @Override
    public List<KeyValue> findFor(Game game, Player player) {
        List<KeyValue> keyValues = new ArrayList<>();

        nemesisAchievement.findNemesis(game, player)
                .map(nemesis -> KeyValue.builder().key("Nemesis").value(nemesis.getName()).build())
                .ifPresent(keyValues::add);

        Set<Player> minions = nemesisAchievement.findAllMinions(game, player);
        if (!minions.isEmpty()) {
            keyValues.add(KeyValue.builder().key("Minions").value(minions.stream()
                    .map(Player::getName)
                    .sorted()
                    .collect(Collectors.joining(", ")))
                    .build());
        }

        guardianAchievement.findGuardian(game, player)
                .map(g -> KeyValue.builder()
                        .key("Guardian")
                        .value(g.getName())
                        .build())
                .ifPresent(keyValues::add);
        String wards = guardianAchievement.findWards(game, player).stream()
                .map(Player::getName)
                .sorted()
                .collect(Collectors.joining(", "));
        if (!wards.isBlank()) {
            keyValues.add(KeyValue.builder()
                    .key("Wards")
                    .value(wards)
                    .build());
        }

        return keyValues;
    }
}
