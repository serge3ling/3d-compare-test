package com.tdc.test.api;

import lombok.Data;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

@Data
public class CommentThread {
    @NonNull private final Collection<String> authors;
    @NonNull private final List<Comment> comments;
}
