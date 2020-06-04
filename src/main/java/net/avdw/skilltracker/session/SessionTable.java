package net.avdw.skilltracker.session;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.util.Date;

@DatabaseTable(tableName = "Session")
public class SessionTable {
    public static final String SESSION_ID = "sessionId";
    public static final String GAME_FK = "gameFk";
    public static final String PLAYER_FK = "playerFk";
    public static final String TEAM = "team";
    public static final String RANK = "rank";
    public static final String PLAY_DATE = "playDate";
    public static final String MEAN = "mean";
    public static final String STANDARD_DEVIATION = "standardDeviation";

    @DatabaseField(columnName = SESSION_ID, canBeNull = false)
    private String sessionId;
    @DatabaseField(columnName = GAME_FK, canBeNull = false)
    private Integer gameFk;
    @DatabaseField(columnName = PLAYER_FK, canBeNull = false)
    private Integer playerFk;
    @DatabaseField(canBeNull = false)
    private Integer team;
    @DatabaseField(canBeNull = false)
    private Integer rank;
    @DatabaseField(canBeNull = false)
    private Date playDate;
    @DatabaseField(canBeNull = false)
    private BigDecimal mean;
    @DatabaseField(canBeNull = false)
    private BigDecimal standardDeviation;

    public SessionTable(Integer gameFk, Integer playerFk, Integer team, Integer rank, Date playDate, BigDecimal mean, BigDecimal standardDeviation) {
        this.gameFk = gameFk;
        this.playerFk = playerFk;
        this.team = team;
        this.rank = rank;
        this.playDate = playDate;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    public SessionTable() {
    }

    public BigDecimal getMean() {
        return mean;
    }

    public void setMean(BigDecimal mean) {
        this.mean = mean;
    }

    public BigDecimal getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(BigDecimal standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public Integer getGameFk() {
        return gameFk;
    }

    public void setGameFk(Integer gameFk) {
        this.gameFk = gameFk;
    }

    public Integer getPlayerFk() {
        return playerFk;
    }

    public void setPlayerFk(Integer playerFk) {
        this.playerFk = playerFk;
    }

    public Integer getTeam() {
        return team;
    }

    public void setTeam(Integer team) {
        this.team = team;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Date getPlayDate() {
        return playDate;
    }

    public void setPlayDate(Date playDate) {
        this.playDate = playDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
