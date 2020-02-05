package com.tdc.test.impl;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("comment_threads")
public class CommentThreadEntity {
    @Id private final String id;
    @NonNull private final String sourceType;
    @NonNull private final String sourceId;

    // I know, I know.
    /*public CommentThreadEntity(String id, @NonNull String sourceType, @NonNull String sourceId) {
        this.id = id;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }

    public String getId() {
        return id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }*/
}
