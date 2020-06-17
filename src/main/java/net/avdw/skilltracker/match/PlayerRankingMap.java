package net.avdw.skilltracker.match;

import de.gesundkrank.jskills.IPlayer;
import de.gesundkrank.jskills.ITeam;
import de.gesundkrank.jskills.Rating;

import java.util.HashMap;

public class PlayerRankingMap extends HashMap<IPlayer, Rating> implements ITeam {
    private static final long serialVersionUID = 1741153417170419910L;
}
