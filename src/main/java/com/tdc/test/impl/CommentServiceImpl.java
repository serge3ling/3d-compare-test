package com.tdc.test.impl;

import com.tdc.test.api.CommentService;
import com.tdc.test.api.CommentThread;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
class CommentServiceImpl implements CommentService {

    private final CommentThreadRepository repository;

    CommentServiceImpl(CommentThreadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createThread(String sourceType, String sourceId, Collection<String> authors) {
        // TODO
    }

    @Override
    public CommentThread getThread(String sourceType, String sourceId) {
        // TODO
        return null;
    }

    @Override
    public String addComment(String sourceType, String sourceId, String author, String text) {
        // TODO
        return UUID.randomUUID().toString();
    }

    @Override
    public void updateComment(String sourceType, String sourceId, String commentId, String text) {
        // TODO
    }

    @Override
    public void deleteComment(String sourceType, String sourceId, String commentId) {
        // TODO
    }
}
