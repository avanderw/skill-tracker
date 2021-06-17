package net.avdw.skilltracker;

import com.google.inject.AbstractModule;
import net.avdw.skilltracker.adapter.out.ormlite.*;
import net.avdw.skilltracker.app.ContestantService;
import net.avdw.skilltracker.app.service.*;
import net.avdw.skilltracker.port.in.query.*;
import net.avdw.skilltracker.port.in.query.stat.*;
import net.avdw.skilltracker.port.out.*;

public class HexagonalModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(StatsQuery.class).to(StatsService.class);
        bind(NemesisQuery.class).to(NemesisService.class);
        bind(MinionQuery.class).to(NemesisService.class);
        bind(MatchupQuery.class).to(MatchupService.class);
        bind(OpponentQuery.class).to(OpponentService.class);
        bind(OpponentRepo.class).to(OpponentRepoAdapter.class);
        bind(SkillQuery.class).to(SkillService.class);
        bind(SkillRepo.class).to(SkillRepoAdapter.class);
        bind(RankQuery.class).to(RankService.class);
        bind(RankRepo.class).to(RankRepoAdapter.class);
        bind(MatchQuery.class).to(MatchService.class);
        bind(MatchRepo.class).to(MatchRepoAdapter.class);
        bind(ContestantRepo.class).to(ContestantRepoAdapter.class);
        bind(ContestantQuery.class).to(ContestantService.class);
        bind(ComradeQuery.class).to(ComradeService.class);
        bind(AllyRepo.class).to(AllyRepoAdapter.class);
        bind(EnthusiastQuery.class).to(EnthusiastService.class);
        bind(ObsessionQuery.class).to(EnthusiastService.class);
        bind(GuardianQuery.class).to(GuardianService.class);
        bind(DominatorQuery.class).to(DominatorService.class);
    }
}
