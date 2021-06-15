package net.avdw.skilltracker.cli.player.view;

import net.avdw.skilltracker.cli.player.model.PlayerDetailModel;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.PriorityObject;
import net.avdw.skilltracker.domain.Stat;
import org.ocpsoft.prettytime.PrettyTime;

public class PlayerDetailView {
    private static final String BASE_PROFILE = "" +
            "%s has played a total of %s games over %s matches%n" +
            "Last played: %s, %s (%s)%n";
    private static final String TOP_GAME_BY_SKILL_HR = "%n" +
            "Top %d skilled games:%n";
    private static final String TOP_GAME_BY_SKILL_LI = "" +
            "> %s at %.1f%n";
    private static final String TOP_GAME_BY_RANK_HR = "%n" +
            "Top %d ranked games:%n";
    private static final String TOP_GAME_BY_RANK_LI = "" +
            "> #%d in %s%n";
    private static final String STATISTIC_HR = "%n" +
            "Statistics%n";
    private static final String STATISTIC_LI = "" +
            "> %s: %s%n";

    public String render(PlayerDetailModel model) {
        StringBuilder render = new StringBuilder(String.format(BASE_PROFILE,
                model.getPlayer().getName(), model.getTotalGames(), model.getTotalMatches(),
                model.getLastPlayedGame().getName(), model.getLastPlayedDate(), new PrettyTime().format(model.getLastPlayedDate())));

        render.append(String.format(TOP_GAME_BY_SKILL_HR, model.getSkilledGames().size()));
        for (PriorityObject<Game> priorityObject : model.getSkilledGames()) {
            render.append(String.format(TOP_GAME_BY_SKILL_LI, priorityObject.getObject().getName(), priorityObject.getPriority().doubleValue()));
        }

        render.append(String.format(TOP_GAME_BY_RANK_HR, model.getRankedGames().size()));
        for (PriorityObject<Game> priorityObject : model.getRankedGames()) {
            render.append(String.format(TOP_GAME_BY_RANK_LI, priorityObject.getPriority().intValue(), priorityObject.getObject().getName()));
        }

        if (!model.getAllStats().isEmpty()) {
            render.append(String.format(STATISTIC_HR));
            for (Stat stat : model.getAllStats()) {
                render.append(String.format(STATISTIC_LI, stat.getName(), stat.getValue()));
            }
        }

        return render.toString();
    }
}
