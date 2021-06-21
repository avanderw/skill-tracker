package net.avdw.skilltracker.cli.player.view;

import lombok.Builder;
import lombok.Value;
import net.avdw.skilltracker.domain.Player;

import java.time.LocalDate;
import java.util.List;

@Value @Builder
public class PlayerListDetailModel {
    List<ListItem> players;

    @Value @Builder
    public static class ListItem {
        Player player;
        LocalDate lastPlayed;
        Integer totalGames;
        Integer totalMatches;
    }
}
