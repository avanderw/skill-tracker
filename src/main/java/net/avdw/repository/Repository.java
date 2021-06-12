package net.avdw.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    void add(T item);

    void addAll(List<T> itemList);

    List<T> findAll();
    List<T> findAll(Specification<T> specification);
    Optional<T> findAny(Specification<T> specification);
    T findFirst(Specification<T> specification, OrderBy orderBy);

    void removeAll(Specification<T> specification);

    void update(T item);

    void commit();
    void setAutoCommit(boolean autoCommit);
}
