package net.avdw.skilltracker.cli.game.view;

import net.avdw.skilltracker.domain.KeyValue;

public class GameDetailView {
    private static final String TOP_PLAYERS_HR = "Top %s players:\n";
    private static final String TOP_PLAYERS_LI = "> %2s: (μ)=%.1f (σ)=%.1f %s\n";
    private static final String STAT_HR = "\nStatistics:\n";
    private static final String STAT_LI = "> %s: %s\n";
    private static final String LAST_MATCHES_HR = "\nLast %s matches:\n";
    private static final String LAST_MATCHES_LI = "> %s (%s) %s\n";

    public String render(GameDetailModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(TOP_PLAYERS_HR, model.getTopPlayers().size()));
        for (GamePlayerModel player : model.getTopPlayers()) {
            sb.append(String.format(TOP_PLAYERS_LI, player.getPosition(), player.getMean(), player.getStdDev(), player.getName()));
        }

        if (model.getGameKeyValues().size() > 0) {
            sb.append(STAT_HR);
            long width = model.getGameKeyValues().stream()
                    .map(KeyValue::getKey)
                    .mapToLong(String::length)
                    .max().orElseThrow();
            for (KeyValue keyValue : model.getGameKeyValues()) {
                sb.append(String.format(STAT_LI, String.format("%" + width + "s", keyValue.getKey()), keyValue.getValue()));
            }
        }

        sb.append(String.format(LAST_MATCHES_HR, model.getMatches().size()));
        for (GameMatchModel match : model.getMatches()) {
            sb.append(String.format(LAST_MATCHES_LI, match.getDate(), match.getSessionId(), match.getTitle()));
        }
        return sb.toString();
    }
}
