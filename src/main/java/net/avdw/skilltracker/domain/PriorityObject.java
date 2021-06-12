package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value @Builder
public class PriorityObject<T> {
    @NonNull T object;
    @NonNull Number priority;
}
