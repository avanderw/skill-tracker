package net.avdw.skilltracker.app.trophy;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.trophy.AllTrophies;
import net.avdw.skilltracker.port.in.query.trophy.DominatorTrophy;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrophyService implements AllTrophies {
    private final DominatorTrophy dominatorTrophy;

    @Inject
    public TrophyService(DominatorTrophy dominatorTrophy) {
        this.dominatorTrophy = dominatorTrophy;
    }

    @Override
    public List<KeyValue> findFor(Player player) {
        List<KeyValue> keyValues = new ArrayList<>();

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
    public List<KeyValue> findFor(Game game, Player player) {
        List<KeyValue> keyValues = new ArrayList<>();

        return keyValues;
    }
}
