package net.avdw.repository.ormlite;

import com.j256.ormlite.stmt.Where;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value @Builder
@EqualsAndHashCode(callSuper = true)
public class AndOrmLiteSpecification<T, I> extends AbstractOrmLiteSpecification<T, I> {
    OrmLiteSpecification<T, I> first;
    OrmLiteSpecification<T, I> second;

    @Override
    public boolean isSatisfiedBy(final T t) {
        return first.isSatisfiedBy(t) && second.isSatisfiedBy(t);
    }

    @Override
    public Class<T> getType() {
        return first.getType();
    }

    @Override
    public String toString() {
        return String.format("(%s && %s)", first, second);
    }

    @Override
    public Where<T, I> toWhere(Where<T, I> where) {
        return where.and(first.toWhere(where), second.toWhere(where));
    }
}
