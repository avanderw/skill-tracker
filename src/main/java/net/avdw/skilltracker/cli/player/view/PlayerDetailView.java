package net.avdw.skilltracker.cli.player.view;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.SneakyThrows;
import net.avdw.skilltracker.StringFormat;
import net.avdw.skilltracker.domain.Game;
import net.avdw.skilltracker.domain.PriorityObject;
import net.avdw.skilltracker.domain.Stat;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerDetailView {
    private static final String PRIMARY_TEMPLATE = "" +
            "{[name}} has played {{total-games}} {{games}} over {{total-matches}} {{matches}}\n" +
            "Last played: {{last-played-game}}, {{last-played-date}} ({{last-played-nlp}})\n" +
            "\n" +
            "| {{top-skilled-games}} | {{top-ranked-games}} |\n" +
            "{{#has-achievement}}\n" +
            "{{achievements-title}}\n" +
            "" +
            "{{/has-achievement}}";

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

    @SneakyThrows
    public String render(PlayerDetailModel model) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache m = mf.compile("net/avdw/skilltracker/cli/player/view/player-detail.mustache");
        Map<String, Object> ctx = new HashMap<>();

        ctx.put("player-name", model.getPlayer().getName());
        ctx.put("tot-games", model.getTotalGames());
        ctx.put("tot-games-pl", model.getTotalGames() == 1 ? "game" : "games");
        ctx.put("tot-matches", model.getTotalMatches());
        ctx.put("tot-matches-pl", model.getTotalGames() == 1 ? "match" : "matches");
        ctx.put("lp-game", model.getLastPlayedGame().getName());
        ctx.put("lp-date", model.getLastPlayedDate());
        ctx.put("lp-date-nlp", new PrettyTime().format(model.getLastPlayedDate()));
        ctx.put("mp-game", StringFormat.camelCaseToTitleCase(model.getMostPlayedGame().getName()));
        ctx.put("ts-games-cnt", model.getSkilledGames().size());
        ctx.put("ts-games-pl", model.getSkilledGames().size() == 1 ? "game" : "games");
        ctx.put("ts-games", model.getSkilledGames().stream().map(po->{
            Map<String, String> item = new HashMap<>();
            item.put("skill", String.format("%2.1f", po.getPriority().doubleValue()));
            item.put("game", StringFormat.camelCaseToTitleCase(po.getObject().getName()));
            return item;
        }).collect(Collectors.toList()));
        ctx.put("tr-games-cnt", model.getRankedGames().size());
        ctx.put("tr-games-pl", model.getRankedGames().size() == 1 ? "game" : "games");
        ctx.put("tr-games", model.getRankedGames().stream().map(po->{
            Map<String, String> item = new HashMap<>();
            item.put("rank", String.format("%2d", po.getPriority().intValue()));
            item.put("game", StringFormat.camelCaseToTitleCase(po.getObject().getName()));
            return item;
        }).collect(Collectors.toList()));
        ctx.put("has-achievements", !model.getAchievements().isEmpty());
        ctx.put("achievements", model.getAchievements());

        StringWriter writer = new StringWriter();
        m.execute(writer, ctx).flush();
        return writer.toString();

//        StringBuilder render = new StringBuilder(String.format(BASE_PROFILE,
//                model.getPlayer().getName(), model.getTotalGames(), model.getTotalMatches(),
//                model.getLastPlayedGame().getName(), model.getLastPlayedDate(), new PrettyTime().format(model.getLastPlayedDate())));
//
//        render.append(String.format(TOP_GAME_BY_SKILL_HR, model.getSkilledGames().size()));
//        for (PriorityObject<Game> priorityObject : model.getSkilledGames()) {
//            render.append(String.format(TOP_GAME_BY_SKILL_LI, priorityObject.getObject().getName(), priorityObject.getPriority().doubleValue()));
//        }
//
//        render.append(String.format(TOP_GAME_BY_RANK_HR, model.getRankedGames().size()));
//        for (PriorityObject<Game> priorityObject : model.getRankedGames()) {
//            render.append(String.format(TOP_GAME_BY_RANK_LI, priorityObject.getPriority().intValue(), priorityObject.getObject().getName()));
//        }
//
//        if (!model.getAllStats().isEmpty()) {
//            render.append(String.format(STATISTIC_HR));
//            long width = model.getAllStats().stream()
//                    .map(Stat::getName)
//                    .mapToLong(String::length)
//                    .max().orElseThrow();
//            for (Stat stat : model.getAllStats()) {
//                render.append(String.format(STATISTIC_LI, String.format("%" + width + "s", stat.getName()), stat.getValue()));
//            }
//        }
//
//        return render.toString();
    }
}
