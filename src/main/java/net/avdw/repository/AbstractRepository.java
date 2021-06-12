package net.avdw.repository;

import java.util.List;
import java.util.Optional;

public class AbstractRepository<T> implements Repository<T> {
    @Override
    public void add(T item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAll(List<T> itemList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> findAll(Specification<T> specification) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<T> findAny(Specification<T> specification) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T findFirst(Specification<T> specification, OrderBy orderBy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAll(Specification<T> specification) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(T item) {
        throw new UnsupportedOperationException();
    }
}
