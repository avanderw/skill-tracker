package net.avdw.repository;

public interface IdType<T> {
    T getId();

    void setId(T id);
}
