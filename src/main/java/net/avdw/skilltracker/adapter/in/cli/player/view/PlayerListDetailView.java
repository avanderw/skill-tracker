package net.avdw.skilltracker.adapter.in.cli.player.view;

import net.avdw.skilltracker.adapter.in.cli.player.model.PlayerListDetailModel;

import java.util.Comparator;

public class PlayerListDetailView {
    private static final String LIST_ITEM = "" +
            "%s %2d-%-3d %s%n";

    public String render(PlayerListDetailModel model) {
        StringBuilder render = new StringBuilder();

        model.getPlayers().stream()
                .sorted(Comparator.comparing(PlayerListDetailModel.ListItem::getLastPlayed).reversed())
                .map(li -> String.format(LIST_ITEM,
                        li.getLastPlayed(),
                        li.getTotalGames(),
                        li.getTotalMatches(),
                        li.getPlayer().getName()))
                .forEach(render::append);

        return render.toString();
    }

}
