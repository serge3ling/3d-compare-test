package com.tdc.test.api;

import java.util.Collection;

public interface CommentService {
    void createThread(String sourceType, String sourceId, Collection<String> authors);

    CommentThread getThread(String sourceType, String sourceId);

    /**
     * @return created comment id
     */
    String addComment(String sourceType, String sourceId, String author, String text);

    void updateComment(String sourceType, String sourceId, String commentId, String text);

    void deleteComment(String sourceType, String sourceId, String commentId);
}
