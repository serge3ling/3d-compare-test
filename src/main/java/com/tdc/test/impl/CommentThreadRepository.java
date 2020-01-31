package com.tdc.test.impl;

import org.springframework.data.repository.Repository;

import java.util.Optional;

interface CommentThreadRepository extends Repository<CommentThreadEntity, String> {
    Optional<CommentThreadEntity> findBySourceTypeAndSourceId(String sourceType, String sourceId);
}
