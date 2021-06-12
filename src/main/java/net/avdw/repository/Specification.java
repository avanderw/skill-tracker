package net.avdw.repository;

/**
 * Lightweight repository pattern.
 * https://dzone.com/articles/java-using-specification
 *
 * @version 2020-08-26: Basic setup as suggested in the article
 * @see AbstractSpecification
 */
public interface Specification<T> {
    boolean isSatisfiedBy(T t);

    Specification<T> and(Specification<T> other);

    Specification<T> or(Specification<T> other);

    Specification<T> not(Specification<T> other);

    Class<T> getType();
}
