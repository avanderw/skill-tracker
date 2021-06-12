package net.avdw.repository;

public interface FileTypeBuilder<T> {
    T build(int idx, String line);
}
