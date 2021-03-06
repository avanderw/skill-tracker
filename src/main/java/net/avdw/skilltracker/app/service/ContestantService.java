package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.query.ContestantQuery;
import net.avdw.skilltracker.port.in.query.SkillQuery;
import net.avdw.skilltracker.port.out.ContestantRepo;

import javax.inject.Inject;
import java.util.List;

public class ContestantService implements ContestantQuery {
    private final ContestantRepo contestantRepo;
    private final SkillQuery skillQuery;

    @Inject
    public ContestantService(ContestantRepo contestantRepo, SkillQuery skillQuery) {
        this.contestantRepo = contestantRepo;
        this.skillQuery = skillQuery;
    }

    @Override
    public List<Contestant> topContestantsBySkill(Game game, Long limit) {
        return contestantRepo.topContestantsBySkill(game, limit);
    }

    @Override
    public Contestant findContestant(Game game, Player player) {
        return Contestant.builder()
                .game(game)
                .player(player)
                .playCount(contestantRepo.playCount(game, player))
                .winCount(contestantRepo.winCount(game, player))
                .winStreak(contestantRepo.winStreak(game, player))
                .skill(skillQuery.findLatest(game, player))
                .build();
    }

    @Override
    public Game mostPlayedGame(Player player) {
        return contestantRepo.mostPlayed(player).getGame();
    }
}
