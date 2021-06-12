package net.avdw.repository;

public class Any<T> extends AbstractSpecification<T> {
    @Override
    public boolean isSatisfiedBy(final T todo) {
        return true;
    }

    @Override
    public String toString() {
        return "any";
    }
}
