package net.avdw.repository;

public class NotSpecification<T> extends AbstractSpecification<T> {
    private final Specification<T> first;
    private final Specification<T> second;

    public NotSpecification(final Specification<T> first, final Specification<T> second) {
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
}
