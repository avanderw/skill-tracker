package net.avdw.skilltracker.player;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import de.gesundkrank.jskills.IPlayer;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.avdw.skilltracker.match.MatchTable;

@Data
@DatabaseTable(tableName = "Player")
@NoArgsConstructor
public class PlayerTable implements IPlayer {
    public static final String NAME = "name";

    @ForeignCollectionField(columnName = MatchTable.PLAYER_FK)
    private ForeignCollection<MatchTable> matchTableList;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(generatedId = true)
    private Integer pk;

    public PlayerTable(final String name) {
        this.name = name;
    }
}
