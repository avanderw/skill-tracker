package net.avdw.skilltracker.maintenance;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import net.avdw.skilltracker.MainModule;
import net.avdw.skilltracker.adapter.out.ormlite.entity.PlayEntity;
import net.avdw.skilltracker.match.MatchService;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlignMatchSessionDates implements Runnable {
    private final MatchService matchService;
    private final Dao<PlayEntity, Integer> matchDao;

    public static void main(final String[] args) {
        Guice.createInjector(new MainModule()).getInstance(AlignMatchSessionDates.class).run();
    }

    @Inject
    AlignMatchSessionDates(final MatchService matchService, final Dao<PlayEntity, Integer> matchDao) {
        this.matchService = matchService;
        this.matchDao = matchDao;
    }

    @Override
    public void run() {
        Map<String, List<PlayEntity>> groupBySessionId = matchService.retrieveAllMatches().stream().collect(Collectors.groupingBy(PlayEntity::getSessionId));
        groupBySessionId.forEach((sessionId, matchList) -> {
            Date date = matchList.stream().findAny().orElseThrow().getPlayDate();
            matchList.forEach(m -> {
                m.setPlayDate(date);
                try {
                    matchDao.update(m);
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
        });

    }
}
