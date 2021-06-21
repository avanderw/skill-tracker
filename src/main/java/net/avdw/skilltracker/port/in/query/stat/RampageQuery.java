package net.avdw.skilltracker.port.in.query.stat;

public interface RampageQuery extends GenericStatQuery {
    @Override
    default String getTitle() {
        return "Rampage";
    }

    @Override
    default String getDescription() {
        return "A Rampage is a 7 match win streak";
    }
}
