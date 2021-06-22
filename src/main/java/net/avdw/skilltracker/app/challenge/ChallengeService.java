package net.avdw.skilltracker.app.challenge;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.challenge.AllChallenges;

import java.util.ArrayList;
import java.util.List;

public class ChallengeService implements AllChallenges {
    @Override
    public List<KeyValue> findFor(Player player) {
        List<KeyValue> keyValues = new ArrayList<>();


        return keyValues;
    }

    @Override
    public List<KeyValue> findFor(Game game, Player player) {
        List<KeyValue> keyValues = new ArrayList<>();


        return keyValues;
    }
}
