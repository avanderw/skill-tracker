package net.avdw.skilltracker.app.challenge;

import net.avdw.skilltracker.domain.KeyValue;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.challenge.AllChallenges;

import java.util.ArrayList;
import java.util.List;

public class ChallengeService implements AllChallenges {
    @Override
    public List<KeyValue> forPlayer(Player player) {
        List<KeyValue> keyValues = new ArrayList<>();


        return keyValues;
    }
}
