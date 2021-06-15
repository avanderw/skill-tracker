package net.avdw.skilltracker.adapter.out.ormlite.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor @NoArgsConstructor
@DatabaseTable(tableName = "Play")
public class PlayEntity {
    public static final String GAME_NAME = "GameName";
    public static final String SESSION_ID = "SessionId";
    public static final String TEAM_RANK = "TeamRank";
    public static final String PLAY_DATE = "PlayDate";
    public static final String PLAYER_NAME = "PlayerName";
    public static final String PLAYER_TEAM = "PlayerTeam";
    public static final String PLAYER_MEAN = "PlayerMean";
    public static final String PLAYER_STD_DEV = "PlayerStdDev";

    @DatabaseField(generatedId = true, canBeNull = false) private Integer pk;
    @DatabaseField(canBeNull = false) private String gameName;
    @DatabaseField(canBeNull = false) private String sessionId;
    @DatabaseField(canBeNull = false) private Integer teamRank;
    @DatabaseField(canBeNull = false) private Date playDate;
    @DatabaseField(canBeNull = false) private String playerName;
    @DatabaseField(canBeNull = false) private Integer playerTeam;
    @DatabaseField(canBeNull = false) private BigDecimal playerMean;
    @DatabaseField(canBeNull = false) private BigDecimal playerStdDev;
}
