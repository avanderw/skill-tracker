package net.avdw.skilltracker.cli.player.view;

import net.avdw.skilltracker.domain.Player;
import org.apache.commons.lang3.StringUtils;

public class PlayerListView {

    public String render(PlayerListModel model) {
        StringBuilder render = new StringBuilder();

        int maxNameSize = model.getPlayers().stream().mapToInt(p -> p.getName().length()).max().orElseThrow() + 1;
        int columns = 5;
        final int[] i = {1};
        model.getPlayers().stream()
                .map(Player::getName)
                .sorted()
                .forEach(player -> {
            render.append(StringUtils.rightPad(player, maxNameSize));
            if (i[0] % columns == 0) {
                render.append("\n");
            }
            i[0]++;
        });

        return render.toString();
    }
}
