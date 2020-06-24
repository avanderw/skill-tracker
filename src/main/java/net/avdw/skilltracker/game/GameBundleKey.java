package net.avdw.skilltracker.game;

public final class GameBundleKey {
    public static final String ADD_SUCCESS = "add.success";
    public static final String DELETE_SUCCESS = "delete.success";
    public static final String GAME_EXIST_FAILURE = "game.exist.failure";
    public static final String GAME_TITLE = "game.title";
    public static final String NO_GAME_FOUND = "no.game.found";
    public static final String NO_MATCH_FOUND = "no.match.found";
    public static final String VIEW_GAME_TOP_PLAYER_LIST_TITLE = "view.game.top.player.list.title(limit)";
    public static final String VIEW_GAME_TOP_PLAYER_LIST_ENTRY = "view.game.top.player.list.entry(name,mean,stdev,rank)";
    public static final String VIEW_GAME_MATCH_LIST_TITLE = "view.game.match.list.title(limit)";
    public static final String VIEW_GAME_MATCH_LIST_ENTRY = "view.game.match.list.entry(date,teams,session)";
    public static final String MATCH_TEAM_ENTRY = "match.team.entry(rank,team)";
    public static final String MATCH_TEAM_PLAYER_ENTRY = "match.team.player.entry(rank,name,mean)";
    public static final String NO_ZERO_DRAW_PROBABILITY = "no.zero.draw.probability";

    private GameBundleKey() {
    }
}
