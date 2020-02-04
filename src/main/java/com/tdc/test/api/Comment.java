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

    // Yeah, boilerplate. TODO.
    public Comment(@NonNull String id, @NonNull String author, @NonNull Instant createdAt, @NonNull Instant updatedAt, @NonNull String text) {
        this.id = id;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getText() {
        return text;
    }
}
