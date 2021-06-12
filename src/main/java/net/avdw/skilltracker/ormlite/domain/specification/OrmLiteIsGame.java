package net.avdw.skilltracker.ormlite.domain.specification;

import com.j256.ormlite.stmt.Where;
import lombok.*;
import net.avdw.repository.ormlite.AbstractOrmLiteSpecification;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteGame;
import net.avdw.skilltracker.adapter.out.ormlite.entity.OrmLiteMatch;

@Value @Builder
@EqualsAndHashCode(callSuper = true)
public class OrmLiteIsGame extends AbstractOrmLiteSpecification<OrmLiteMatch, Integer> {
    @NonNull OrmLiteGame ormLiteGame;

    @Override
    public boolean isSatisfiedBy(OrmLiteMatch ormLiteMatch) {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    @Override
    public Where<OrmLiteMatch, Integer> toWhere(Where<OrmLiteMatch, Integer> where) {
        return where.eq(OrmLiteMatch.GAME_FK, ormLiteGame);
    }
}
