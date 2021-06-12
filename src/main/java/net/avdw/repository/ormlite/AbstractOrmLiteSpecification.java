package net.avdw.repository.ormlite;

import net.avdw.repository.AbstractSpecification;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractOrmLiteSpecification<T, I> extends AbstractSpecification<T> implements OrmLiteSpecification<T, I> {
    public OrmLiteSpecification<T, I> and(final OrmLiteSpecification<T, I> other) {
        return new AndOrmLiteSpecification<>(this, other);
    }

    public OrmLiteSpecification<T, I> or(final  OrmLiteSpecification<T, I> other) {
        return new OrOrmLiteSpecification<>(this, other);
    }

    public OrmLiteSpecification<T, I> not(final  OrmLiteSpecification<T, I> other) {
        return new NotOrmLiteSpecification<>(this, other);
    }

    @Override
    public Class<T> getType() {
        final ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }
}
