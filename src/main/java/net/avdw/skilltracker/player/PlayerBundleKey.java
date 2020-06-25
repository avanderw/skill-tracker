package net.avdw.skilltracker.player;

public final class PlayerBundleKey {

    public static final String MATCH_TEAM_ENTRY = "match.team.entry(rank,team)";
    public static final String MATCH_TEAM_PLAYER_ENTRY = "match.team.player.entry(rank,name,mean)";
    public static final String NO_MATCH_FOUND = "no.match.found";
    public static final String PLAYER_LAST_PLAYED_ENTRY = "player.last.played.list.entry(date,game,team,session)";
    public static final String PLAYER_LAST_PLAYED_TITLE = "player.last.played.list.title(limit)";
    public static final String PLAYER_NOT_EXIST = "player.not.exist";
    public static final String PLAYER_TITLE = "player.title";
    public static final String PLAYER_TOP_GAME_LIST_ENTRY = "player.top.game.list.entry(name,mean,stdev,rank)";
    public static final String PLAYER_TOP_GAME_LIST_TITLE = "player.top.game.list.title(limit)";

    private PlayerBundleKey() {

    }
}
