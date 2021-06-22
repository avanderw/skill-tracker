package net.avdw.skilltracker;

import com.google.inject.AbstractModule;
import net.avdw.skilltracker.adapter.out.ormlite.*;
import net.avdw.skilltracker.app.achievement.AchievementService;
import net.avdw.skilltracker.app.achievement.GuardianService;
import net.avdw.skilltracker.app.achievement.NemesisService;
import net.avdw.skilltracker.app.badge.BadgesService;
import net.avdw.skilltracker.app.badge.ComradeService;
import net.avdw.skilltracker.app.badge.EnthusiastService;
import net.avdw.skilltracker.app.challenge.ChallengeService;
import net.avdw.skilltracker.app.service.*;
import net.avdw.skilltracker.app.statistic.HIndexService;
import net.avdw.skilltracker.app.trophy.DominatorService;
import net.avdw.skilltracker.app.trophy.TrophyService;
import net.avdw.skilltracker.port.in.query.*;
import net.avdw.skilltracker.port.in.query.achievement.AllAchievements;
import net.avdw.skilltracker.port.in.query.achievement.GuardianAchievement;
import net.avdw.skilltracker.port.in.query.achievement.NemesisAchievement;
import net.avdw.skilltracker.port.in.query.badge.AllBadges;
import net.avdw.skilltracker.port.in.query.badge.ComradeBadge;
import net.avdw.skilltracker.port.in.query.badge.EnthusiastBadge;
import net.avdw.skilltracker.port.in.query.challenge.AllChallenges;
import net.avdw.skilltracker.port.in.query.statistic.HIndexStatistic;
import net.avdw.skilltracker.port.in.query.trophy.AllTrophies;
import net.avdw.skilltracker.port.in.query.trophy.DominatorTrophy;
import net.avdw.skilltracker.port.out.*;

public class HexagonalModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(StatsQuery.class).to(StatsService.class);
        bind(NemesisAchievement.class).to(NemesisService.class);
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
        bind(ComradeBadge.class).to(ComradeService.class);
        bind(AllyRepo.class).to(AllyRepoAdapter.class);
        bind(EnthusiastBadge.class).to(EnthusiastService.class);
        bind(GuardianAchievement.class).to(GuardianService.class);
        bind(DominatorTrophy.class).to(DominatorService.class);
        bind(AllTrophies.class).to(TrophyService.class);
        bind(AllChallenges.class).to(ChallengeService.class);
        bind(AllAchievements.class).to(AchievementService.class);
        bind(AllBadges.class).to(BadgesService.class);
        bind(HIndexStatistic.class).to(HIndexService.class);
        bind(PlayRepo.class).to(PlayRepoAdapter.class);
    }
}
