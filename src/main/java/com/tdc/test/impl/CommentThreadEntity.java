package com.tdc.test.impl;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("comment_threads")
class CommentThreadEntity {
    @Id private final String id;
    @NonNull private final String sourceType;
    @NonNull private final String sourceId;
}
