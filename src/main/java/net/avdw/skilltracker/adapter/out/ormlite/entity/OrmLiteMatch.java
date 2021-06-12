package net.avdw.skilltracker.adapter.out.ormlite.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@DatabaseTable(tableName = "Match")
@NoArgsConstructor
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
public class OrmLiteMatch {
    public static final String PK = "pk";
    public static final String GAME_FK = "gameFk";
    public static final String MEAN = "mean";
    public static final String PLAYER_FK = "playerFk";
    public static final String PLAY_DATE = "playDate";
    public static final String RANK = "rank";
    public static final String SESSION_ID = "sessionId";
    public static final String STANDARD_DEVIATION = "standardDeviation";
    public static final String TEAM = "team";

    @DatabaseField(columnName = GAME_FK, foreign = true, foreignAutoRefresh = true)
    private OrmLiteGame game;
    @DatabaseField(canBeNull = false)
    private BigDecimal mean;
    @DatabaseField(canBeNull = false)
    private Date playDate;
    @DatabaseField(columnName = PLAYER_FK, foreign = true, foreignAutoRefresh = true)
    private OrmLitePlayer player;
    @DatabaseField(canBeNull = false)
    private Integer rank;
    @DatabaseField(columnName = SESSION_ID, canBeNull = false)
    private String sessionId;
    @DatabaseField(canBeNull = false)
    private BigDecimal standardDeviation;
    @DatabaseField(canBeNull = false)
    private Integer team;
    @ToString.Exclude
    @DatabaseField(generatedId = true)
    private Integer pk;

    public Date getPlayDate() {
        return new Date(playDate.getTime());
    }

    public void setPlayDate(final Date playDate) {
        this.playDate = new Date(playDate.getTime());
    }
}



