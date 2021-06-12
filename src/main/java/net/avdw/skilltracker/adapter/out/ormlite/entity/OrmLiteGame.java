package net.avdw.skilltracker.adapter.out.ormlite.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.math.BigDecimal;

@Data
@DatabaseTable(tableName = "Game")
@NoArgsConstructor
@RequiredArgsConstructor
public class OrmLiteGame {
    public static final String NAME = "Name";

    @DatabaseField
    @NonNull
    private BigDecimal drawProbability;
    @ToString.Exclude
    @ForeignCollectionField(columnName = OrmLiteMatch.GAME_FK)
    private ForeignCollection<OrmLiteMatch> ormLiteMatchList;
    @DatabaseField
    @NonNull
    private String name;
    @ToString.Exclude
    @DatabaseField(generatedId = true)
    private Integer pk;

    public OrmLiteGame(String name) {
        this.name = name;
    }
}
