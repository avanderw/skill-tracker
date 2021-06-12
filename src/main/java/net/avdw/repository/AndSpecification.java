package net.avdw.repository;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class AndSpecification<T> extends AbstractSpecification<T> {
    Specification<T> first;
    Specification<T> second;

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
}
