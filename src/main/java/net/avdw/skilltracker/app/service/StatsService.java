package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.port.in.query.StatsQuery;
import net.avdw.skilltracker.port.in.query.achievement.GuardianAchievement;
import net.avdw.skilltracker.port.in.query.achievement.NemesisAchievement;
import net.avdw.skilltracker.port.in.query.badge.ComradeBadge;
import net.avdw.skilltracker.port.in.query.badge.EnthusiastBadge;
import net.avdw.skilltracker.port.in.query.trophy.DominatorTrophy;
import net.avdw.skilltracker.port.out.ContestantRepo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatsService implements StatsQuery {
    private final NemesisAchievement nemesisAchievement;
    private final ComradeBadge comradeBadge;
    private final ContestantRepo contestantRepo;
    private final EnthusiastBadge enthusiastBadge;
    private final GuardianAchievement guardianAchievement;
    private final DominatorTrophy dominatorTrophy;

    @Inject
    public StatsService(                        NemesisAchievement nemesisAchievement,
                        ComradeBadge comradeBadge,
                        ContestantRepo contestantRepo,
                        EnthusiastBadge enthusiastBadge,
                        GuardianAchievement guardianAchievement,
                        DominatorTrophy dominatorTrophy) {
        this.nemesisAchievement = nemesisAchievement;
        this.comradeBadge = comradeBadge;
        this.contestantRepo = contestantRepo;
        this.enthusiastBadge = enthusiastBadge;
        this.guardianAchievement = guardianAchievement;
        this.dominatorTrophy = dominatorTrophy;
    }

    @Override
    public List<KeyValue> gameStatsForPlayer(Game game, Player player) {
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

        comradeBadge.findComrade(game, player)
                .map(p -> KeyValue.builder()
                        .key("Comrade")
                        .value(p.getName())
                        .build())
                .ifPresent(keyValues::add);

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

    @Override
    public List<KeyValue> playerStats(Player player) {
        List<KeyValue> keyValues = new ArrayList<>();

        comradeBadge.findComrade(player)
                .map(p -> KeyValue.builder()
                        .key("Comrade")
                        .value(p.getName())
                        .build())
                .ifPresent(keyValues::add);

        String obsession = enthusiastBadge.findObsession(player).stream()
                .map(Game::getName)
                .collect(Collectors.joining(", "));
        if (!obsession.isBlank()) {
            keyValues.add(KeyValue.builder()
                    .key("Obsession")
                    .value(obsession)
                    .build());
        }

        String dominating = dominatorTrophy.findDominating(player).stream()
                .map(Game::getName)
                .sorted()
                .collect(Collectors.joining(", "));
        if (!dominating.isBlank()) {
            keyValues.add(KeyValue.builder()
                    .key("Dominating")
                    .value(dominating)
                    .build());
        }

        return keyValues;
    }

    @Override
    public List<KeyValue> gameStats(Game game) {
        List<KeyValue> keyValues = new ArrayList<>();

        enthusiastBadge.findEnthusiast(game)
                .map(p -> KeyValue.builder()
                        .key("Enthusiast")
                        .value(p.getName())
                        .build())
                .ifPresent(keyValues::add);

        dominatorTrophy.findDominator(game)
                .map(p -> KeyValue.builder()
                        .key("Dominator")
                        .value(p.getName())
                        .build())
                .ifPresent(keyValues::add);

        return keyValues;
    }

}
