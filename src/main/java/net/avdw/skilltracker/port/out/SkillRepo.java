package net.avdw.skilltracker.port.out;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Skill;

public interface SkillRepo {
    Skill findBy(Game game, Player player);
}
