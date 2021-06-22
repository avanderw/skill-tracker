package net.avdw.skilltracker.port.in.query.achievement;

public interface RampageAchievement extends Achievement {
    @Override
    default String getTitle() {
        return "Rampage";
    }

    @Override
    default String getDescription() {
        return "A Rampage is a 7 match win streak";
    }
}
