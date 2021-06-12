package net.avdw.skilltracker.port.in;

import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Skill;

public interface SkillQuery {
    Skill findLatest(Game game, Player player);
}
