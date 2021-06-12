package net.avdw.skilltracker.adapter.out.ormlite;

import net.avdw.repository.AbstractRepository;
import net.avdw.skilltracker.domain.Match;
import net.avdw.skilltracker.port.out.MatchRepository;

import javax.inject.Inject;

public class MatchRepositoryAdapter extends AbstractRepository<Match> implements MatchRepository {

    @Inject
    public MatchRepositoryAdapter() {
    }

}
