package net.avdw.skilltracker.cli.player.view;

import net.avdw.skilltracker.domain.Contestant;
import net.avdw.skilltracker.domain.Skill;
import net.avdw.skilltracker.domain.Stat;
import org.ocpsoft.prettytime.PrettyTime;

public class PlayerGameView {
    private static final String PROFILE = "" +
            "%s is #%s in %s with %d matches%n" +
            "Last played: %s (%s)%n" +
            " Win streak: %d / %d%n" +
            "      Skill: %4.1f%n" +
            "       Mean: %4.1fμ%n" +
            "     Stddev: %4.1fσ%n";
    private static final String STAT_HEADER = "%n" +
            "Statistics%n";
    private static final String STAT = "" +
            "> %s: %s%n";

    public String render(PlayerGameModel model) {
        Contestant c = model.getContestant();
        Skill skill = c.getSkill();
        StringBuilder render = new StringBuilder(String.format(PROFILE,
                c.getPlayer().getName(), model.getContestantRank(), c.getGame().getName(), model.getTotalMatches(),
                skill.getDate(), new PrettyTime().format(skill.getDate()),
                c.getWinStreak().getCurrent(), c.getWinStreak().getLongest(),
                skill.getLow(), skill.getMean(), skill.getStdDev()));

        if (!model.getStats().isEmpty()) {
            render.append(String.format(STAT_HEADER));
            long width = model.getStats().stream()
                    .map(Stat::getName)
                    .mapToLong(String::length)
                    .max().orElseThrow();
            for (Stat stat : model.getStats()) {
                render.append(String.format(STAT, String.format("%" + width + "s", stat.getName()), stat.getValue()));
            }
        }

        return render.toString();
    }
}


