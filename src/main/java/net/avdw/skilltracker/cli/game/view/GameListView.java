package net.avdw.skilltracker.cli.game.view;

import net.avdw.skilltracker.StringFormat;
import net.avdw.skilltracker.domain.Game;
import org.apache.commons.lang3.StringUtils;

public class GameListView {
    public String render(GameListModel model) {
        StringBuilder render = new StringBuilder();

        int maxNameSize = Double.valueOf(model.getGames().stream().mapToInt(g -> StringFormat.camelCaseToTitleCase(g.getName()).length()).max().orElseThrow() * 1.2).intValue();
        int columns = 3;
        final int[] i = {1};
        model.getGames().stream()
                .map(Game::getName)
                .map(StringFormat::camelCaseToTitleCase)
                .sorted()
                .forEach(game -> {
                    render.append(StringUtils.rightPad(game, maxNameSize));
                    if (i[0] % columns == 0) {
                        render.append("\n");
                    }
                    i[0]++;
                });

        return render.toString();
    }
}
