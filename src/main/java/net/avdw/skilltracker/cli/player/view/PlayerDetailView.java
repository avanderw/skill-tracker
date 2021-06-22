package net.avdw.skilltracker.cli.player.view;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.SneakyThrows;
import net.avdw.skilltracker.StringFormat;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerDetailView {
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
        ctx.put("lp-game", StringFormat.camelCaseToTitleCase(model.getLastPlayedGame().getName()));
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
        ctx.put("has-trophies", !model.getTrophies().isEmpty());
        ctx.put("trophies", model.getTrophies().stream().map(kv->{
            Map<String, String> item = new HashMap<>();
            item.put("trophy", kv.getKey());
            item.put("game", StringFormat.camelCaseToTitleCase(kv.getValue()));
            return item;
        }).collect(Collectors.toList()));
        ctx.put("has-challenges", !model.getChallenges().isEmpty());
        ctx.put("challenges", model.getChallenges().stream().map(kv->{
            Map<String, String> item = new HashMap<>();
            item.put("challenge", kv.getKey());
            item.put("value", kv.getValue());
            return item;
        }).collect(Collectors.toList()));
        ctx.put("has-achievements", !model.getAchievements().isEmpty());
        ctx.put("achievements", model.getAchievements().stream().map(kv->{
            Map<String, String> item = new HashMap<>();
            item.put("achievement", kv.getKey());
            item.put("value", kv.getValue());
            return item;
        }).collect(Collectors.toList()));
        ctx.put("has-badges", !model.getBadges().isEmpty());
        ctx.put("badges", model.getBadges().stream().map(kv->{
            Map<String, String> item = new HashMap<>();
            item.put("badge", kv.getKey());
            item.put("value", kv.getValue());
            return item;
        }).collect(Collectors.toList()));

        StringWriter writer = new StringWriter();
        m.execute(writer, ctx).flush();
        return writer.toString().trim();
    }
}
