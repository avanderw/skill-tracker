package net.avdw.skilltracker.port.in.query;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.Player;
import net.avdw.skilltracker.domain.Skill;

import java.util.List;

public interface SkillQuery {
    Skill findLatest(Game game, Player player);
}
