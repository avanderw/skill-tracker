package net.avdw.skilltracker.app.badge;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.badge.AllBadges;
import net.avdw.skilltracker.port.in.query.badge.ComradeBadge;
import net.avdw.skilltracker.port.in.query.badge.EnthusiastBadge;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BadgesService implements AllBadges {
    private final ComradeBadge comradeBadge;
    private final EnthusiastBadge enthusiastBadge;

    @Inject
    public BadgesService(ComradeBadge comradeBadge, EnthusiastBadge enthusiastBadge) {
        this.comradeBadge = comradeBadge;
        this.enthusiastBadge = enthusiastBadge;
    }

    @Override
    public List<KeyValue> forPlayer(Player player) {
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

        return keyValues;
    }
}
