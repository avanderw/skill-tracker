package net.avdw.skilltracker.match;

public final class MatchBundleKey {
    public static final String CREATE_SUCCESS = "create.success";
    public static final String DELETE_COMMAND = "delete.command(ids)";
    public static final String DELETE_COMMAND_FAILURE = "delete.command.failure";
    public static final String DELETE_ENTRY_FAILURE = "delete.entry.failure(id)";
    public static final String DELETE_ENTRY_SUCCESS = "delete.entry.success(id)";
    public static final String LAST_MATCH_LIST_ENTRY = "last.match.list.entry(date,session,game,teams)";
    public static final String LAST_MATCH_LIST_TITLE = "last.match.list.title(limit)";
    public static final String MATCH_TEAM_ENTRY = "match.team.entry(rank,team)";
    public static final String MATCH_TEAM_PLAYER_ENTRY = "match.team.player.entry(name)";
    public static final String NO_GAME_FOUND = "no.game.found";
    public static final String NO_MATCH_FOUND = "no.match.found";
    public static final String QUALITY_CLI_TITLE = "quality.title(game,quality,summary)";
    public static final String QUALITY_FFA_MATCH_TITLE = "quality.ffa.match.title";
    public static final String QUALITY_HI = "quality.hi";
    public static final String QUALITY_MED = "quality.med";
    public static final String QUALITY_LOW = "quality.low";
    public static final String QUALITY_LOW_THRESHOLD = "quality.low.threshold";
    public static final String QUALITY_MED_THRESHOLD = "quality.med.threshold";
    public static final String QUALITY_TEAM_TITLE = "quality.team.title(mean,stdev)";
    public static final String QUALITY_TEAM_PLAYER_ENTRY = "quality.team.player.entry(mean,stdev,name)";
    public static final String SUGGEST_CLI_TITLE = "suggest.cli.title";
    public static final String SUGGEST_TABLE_QUALITY_HEADER = "suggest.table.quality.header";
    public static final String SUGGEST_TABLE_TEAM_HEADER = "suggest.table.team.header";
    public static final String TEAM_PLAYER_COUNT_MISMATCH = "team.player.count.mismatch";
    public static final String TEAM_RANK_COUNT_MISMATCH = "team.rank.count.mismatch";
    public static final String VIEW_MATCH_DETAIL_PLAYER_ENTRY = "view.match.detail.player.entry(rank,person,mean,stdev)";
    public static final String VIEW_MATCH_DETAIL_TITLE = "view.match.detail.title(id,game,date)";

    private MatchBundleKey() {
    }
}
