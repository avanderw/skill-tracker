package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * A PriorityObject can be added to a PriorityQueue.
 * @param <T> The object that gets priority.
 */
@Value @Builder
public class PriorityObject<T> {
    @NonNull T object;
    @NonNull Number priority;
}
