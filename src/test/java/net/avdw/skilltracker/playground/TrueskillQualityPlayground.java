package net.avdw.skilltracker.playground;

import de.gesundkrank.jskills.*;
import de.gesundkrank.jskills.trueskill.FactorGraphTrueSkillCalculator;

import java.util.Arrays;

public final class TrueskillQualityPlayground {
    public static void main(final String[] args) {
        SkillCalculator factorGraphSkillCalculator = new FactorGraphTrueSkillCalculator();
        Player<String> JK = new Player<>("JK");
        Player<String> Wicus = new Player<>("Wicus");
        System.out.println(factorGraphSkillCalculator.calculateMatchQuality(GameInfo.getDefaultGameInfo(), Arrays.asList(
                new Team().addPlayer(JK, new Rating(50, 5)),
                new Team().addPlayer(Wicus, new Rating(50, 1))
        )));
    }
}
