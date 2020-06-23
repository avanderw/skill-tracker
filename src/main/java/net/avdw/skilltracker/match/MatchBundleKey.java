package net.avdw.skilltracker.match;

public final class MatchBundleKey {
    public static final String DELETE_COMMAND = "delete.command";
    public static final String DELETE_ENTRY_FAILURE = "delete.entry.failure(id)";
    public static final String DELETE_ENTRY_SUCCESS = "delete.entry.success(id)";
    public static final String DELETE_COMMAND_FAILURE = "delete.command.failure";
    public static final String LAST_MATCH_LIST_TITLE = "last.match.list.title(limit)";
    public static final String LAST_MATCH_LIST_ENTRY = "last.match.list.entry(date,session,game,teams)";
    public static final String MATCH_TEAM_ENTRY = "match.team.entry(rank,team)";
    public static final String MATCH_TEAM_PLAYER_ENTRY = "match.team.player.entry(rank,name,mean)";
    public static final String NO_MATCH_FOUND = "no.match.found";
    public static final String SUGGEST_CLI_TITLE = "suggest.cli.title";
    public static final String SUGGEST_TABLE_QUALITY_HEADER = "suggest.table.quality.header";
    public static final String SUGGEST_TABLE_TEAM_HEADER = "suggest.table.team.header";
    public static final String TEAM_PLAYER_COUNT_MISMATCH = "team.player.count.mismatch";
    public static final String TEAM_RANK_COUNT_MISMATCH = "team.rank.count.mismatch";
    public static final String CREATE_SUCCESS = "create.success";

    private MatchBundleKey() {
    }
}
