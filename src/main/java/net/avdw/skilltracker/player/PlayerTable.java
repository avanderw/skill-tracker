package net.avdw.skilltracker.player;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.gesundkrank.jskills.IPlayer;

@DatabaseTable(tableName = "Player")
public class PlayerTable implements IPlayer {
    public static final String NAME = "name";

    @DatabaseField(generatedId = true)
    private Integer pk;
    @DatabaseField(canBeNull = false)
    private String name;

    public PlayerTable() {
    }

    public PlayerTable(final String name) {
        this.name = name;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(final Integer pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
