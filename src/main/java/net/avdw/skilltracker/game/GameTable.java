package net.avdw.skilltracker.game;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;
import net.avdw.skilltracker.match.MatchTable;

import java.math.BigDecimal;

@Data
@DatabaseTable(tableName = "Game")
@NoArgsConstructor
@RequiredArgsConstructor
public class GameTable {
    public static final String NAME = "Name";

    @DatabaseField
    @NonNull
    private BigDecimal drawProbability;
    @ToString.Exclude
    @ForeignCollectionField(columnName = MatchTable.GAME_FK)
    private ForeignCollection<MatchTable> matchTableList;
    @DatabaseField
    @NonNull
    private String name;
    @ToString.Exclude
    @DatabaseField(generatedId = true)
    private Integer pk;
}
