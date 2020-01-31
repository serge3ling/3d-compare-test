package com.tdc.test.api;

import lombok.Data;
import lombok.NonNull;

import java.time.Instant;

@Data
public class Comment {
    @NonNull private final String id;
    @NonNull private final String author;
    @NonNull private final Instant createdAt;
    @NonNull private final Instant updatedAt;
    @NonNull private final String text;
}
