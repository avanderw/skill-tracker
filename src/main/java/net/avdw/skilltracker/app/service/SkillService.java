package net.avdw.skilltracker.app.service;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Skill;
import net.avdw.skilltracker.port.in.query.SkillQuery;
import net.avdw.skilltracker.port.out.SkillRepo;

import javax.inject.Inject;
import java.util.List;

public class SkillService  implements SkillQuery {
    private final SkillRepo skillRepo;

    @Inject
    public SkillService(SkillRepo skillRepo) {
        this.skillRepo = skillRepo;
    }

    @Override
    public Skill findLatest(Game game, Player player) {
        return skillRepo.findBy(game, player);
    }
}
