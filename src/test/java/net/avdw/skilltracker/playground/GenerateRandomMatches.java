package net.avdw.skilltracker.playground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class GenerateRandomMatches {
    public static void main(final String[] args) {
        List<String> characters = Arrays.asList("Naruto", "Sasuke", "Sakura", "Kakashi", "Sasori", "Hinata", "Kakuzu",
                "Madara", "Kankuro", "Hidan", "Orochimaru", "Deidara", "Nagato", "Akamaru", "Kisame", "Zetsu", "Obito",
                "Shino", "Kabuto", "Asuma", "Temari", "Zabuza", "Gaara", "Haku", "Neji", "Choji", "Tenten", "Ino", "Shikamaru");

        Random random
                = new Random();
        int numGames = 100;
        for (int i = 0; i < numGames; i++) {
            List<String> selectionPool = new ArrayList<>(characters);
            List<Integer> rankPool = new ArrayList<>();

            int numTeams = random.nextInt(2) + 2;
            for (int j = 0; j < numTeams; j++) {
                rankPool.add(j + 1);
            }
            List<String> teamList = new ArrayList<>();
            List<String> rankList = new ArrayList<>();
            for (int j = 0; j < numTeams; j++) {
                int numPlayers = random.nextInt(3) + 1;
                List<String> playerList = new ArrayList<>();
                for (int k = 0; k < numPlayers; k++) {
                    playerList.add(selectionPool.remove(random.nextInt(selectionPool.size())));
                }
                teamList.add(String.join(",", playerList));
                rankList.add("" + rankPool.remove(random.nextInt(rankPool.size())));
            }

            System.out.println(String.format("java -jar ./target/skill-tracker.jar match add %s -r=%s -g=Naruto",
                    String.join(" ", teamList),
                    String.join(",", rankList)));
        }
    }
}
