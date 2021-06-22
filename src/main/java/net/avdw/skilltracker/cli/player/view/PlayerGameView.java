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

public class PlayerGameView {
    @SneakyThrows
    public String render(PlayerGameModel model) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache m = mf.compile("net/avdw/skilltracker/cli/player/view/player-game.mustache");
        Map<String, Object> ctx = new HashMap<>();

        ctx.put("player-name", model.getContestant().getPlayer().getName());
        ctx.put("player-rank", model.getContestantRank());
        ctx.put("game", model.getContestant().getGame().getName());
        ctx.put("tot-matches", model.getTotalMatches());
        ctx.put("tot-matches-pl", model.getTotalMatches() == 1 ? "match" : "matches");
        ctx.put("fp-date", model.getFirstPlayedDate());
        ctx.put("fp-date-nlp", new PrettyTime().format(model.getFirstPlayedDate()));
        ctx.put("lp-date", model.getLastPlayedDate());
        ctx.put("lp-date-nlp", new PrettyTime().format(model.getLastPlayedDate()));
        ctx.put("ws-current", model.getContestant().getWinStreak().getCurrent());
        ctx.put("ws-longest", model.getContestant().getWinStreak().getLongest());
        ctx.put("skill", String.format("%2.1f", model.getContestant().getSkill().getLow()));
        ctx.put("mean", String.format("%2.1f", model.getContestant().getSkill().getMean()));
        ctx.put("stddev", String.format("%2.1f", model.getContestant().getSkill().getStdDev()));
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

//        Contestant c = model.getContestant();
//        Skill skill = c.getSkill();
//        StringBuilder render = new StringBuilder(String.format(PROFILE,
//                c.getPlayer().getName(), model.getContestantRank(), c.getGame().getName(), model.getTotalMatches(),
//                skill.getDate(), new PrettyTime().format(skill.getDate()),
//                c.getWinStreak().getCurrent(), c.getWinStreak().getLongest(),
//                skill.getLow(), skill.getMean(), skill.getStdDev()));
//
//        if (!model.getStats().isEmpty()) {
//            render.append(String.format(STAT_HEADER));
//            long width = model.getStats().stream()
//                    .map(KeyValue::getKey)
//                    .mapToLong(String::length)
//                    .max().orElseThrow();
//            for (KeyValue keyValue : model.getStats()) {
//                render.append(String.format(STAT, String.format("%" + width + "s", keyValue.getKey()), keyValue.getValue()));
//            }
//        }
//
//        return render.toString();
    }
}


