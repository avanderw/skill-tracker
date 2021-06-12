package net.avdw.repository.ormlite;

import com.j256.ormlite.stmt.Where;
import net.avdw.repository.Specification;

public interface OrmLiteSpecification<T, I> extends Specification<T> {
    Where<T, I> toWhere(Where<T, I> where);
}
