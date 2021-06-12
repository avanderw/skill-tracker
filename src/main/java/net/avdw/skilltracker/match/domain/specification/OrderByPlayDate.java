package net.avdw.skilltracker.match.domain.specification;

import lombok.Value;
import net.avdw.repository.OrderBy;

@Value
public class OrderByPlayDate implements OrderBy {

    boolean ascending;

    @Override
    public String getFieldName() {
        return "date";
    }
}
