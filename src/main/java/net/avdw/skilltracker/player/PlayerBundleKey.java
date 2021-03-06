package net.avdw.skilltracker.player;

public final class PlayerBundleKey {

    public static final String CHANGE_NAME = "change.name(to,from)";
    public static final String MATCH_TEAM_ENTRY = "match.team.entry(rank,team)";
    public static final String MATCH_TEAM_PLAYER_ENTRY = "match.team.player.entry(name)";
    public static final String PLAYER_LAST_PLAYED_ENTRY = "player.last.played.list.entry(date,game,team,session,mean,stdev)";
    public static final String PLAYER_LAST_PLAYED_TITLE = "player.last.played.list.title(limit)";
    public static final String PLAYER_LIST_TITLE = "player.list.title";
    public static final String PLAYER_NOT_EXIST = "player.not.exist";
    public static final String PLAYER_TITLE = "player.list.entry(gameCount,name)";
    public static final String PLAYER_TOP_GAME_LIST_ENTRY = "player.top.game.list.entry(name,mean,stdev,rank)";
    public static final String PLAYER_TOP_GAME_LIST_TITLE = "player.top.game.list.title(limit)";
    public static final String REPLACE_SAME_PLAYER = "replace.same.player(to,from)";
    public static final String SPECIFIC_GAME_LAST_PLAYED_ENTRY = "specific.game.last.played.entry(date,mean,stdev,teams,session)";
    public static final String SPECIFIC_GAME_TITLE = "specific.game.title(game,person,mean,stdev,played)";
    public static final String WARN_REPLACE_PLAYER = "warn.replace.player(to,from)";

    private PlayerBundleKey() {

    }
}
