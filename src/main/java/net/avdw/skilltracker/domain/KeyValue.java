package net.avdw.skilltracker.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value @Builder
public class KeyValue {
    @NonNull String key;
    @NonNull String value;
}
