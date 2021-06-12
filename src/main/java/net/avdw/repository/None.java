package net.avdw.repository;

public class None<T> extends AbstractSpecification<T> {
    @Override
    public boolean isSatisfiedBy(final T todo) {
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
