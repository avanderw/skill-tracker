package net.avdw.repository.ormlite;

import com.j256.ormlite.stmt.Where;

public class NotOrmLiteSpecification<T, I> extends AbstractOrmLiteSpecification<T, I> {
    private final OrmLiteSpecification<T, I> first;
    private final OrmLiteSpecification<T, I> second;

    public NotOrmLiteSpecification(final OrmLiteSpecification<T, I> first, final OrmLiteSpecification<T, I> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isSatisfiedBy(final T t) {
        return first.isSatisfiedBy(t) && !second.isSatisfiedBy(t);
    }

    @Override
    public Class<T> getType() {
        return first.getType();
    }

    @Override
    public String toString() {
        return String.format("(%s && !%s)", first, second);
    }

    @Override
    public Where<T, I> toWhere(Where<T, I> where) {
        return first.toWhere(where).not(second.toWhere(where));
    }
}
