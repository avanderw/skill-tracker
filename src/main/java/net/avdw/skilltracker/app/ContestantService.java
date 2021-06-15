package net.avdw.skilltracker.app;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.port.in.ContestantQuery;
import net.avdw.skilltracker.port.in.SkillQuery;
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
                .skill(skillQuery.findLatest(game, player))
                .build();
    }
}
