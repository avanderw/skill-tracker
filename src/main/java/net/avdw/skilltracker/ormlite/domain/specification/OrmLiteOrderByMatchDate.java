package net.avdw.skilltracker.ormlite.domain.specification;

import lombok.Builder;
import lombok.Value;
import net.avdw.repository.ormlite.OrmLiteOrderBy;

@Value @Builder
public class OrmLiteOrderByMatchDate implements OrmLiteOrderBy {
    boolean ascending;

    @Override
    public String getFieldName() {
        return "playDate";
    }
}
