package net.avdw.skilltracker.session;

import de.gesundkrank.jskills.IPlayer;
import de.gesundkrank.jskills.ITeam;
import de.gesundkrank.jskills.Rating;

import java.util.HashMap;

public class SessionTeam extends HashMap<IPlayer, Rating> implements ITeam {
}
