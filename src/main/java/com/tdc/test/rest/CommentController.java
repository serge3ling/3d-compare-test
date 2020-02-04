package com.tdc.test.rest;

import com.tdc.test.api.CommentService;
import com.tdc.test.api.CommentThread;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("comments")
class CommentController {
    private final CommentService service;

    CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping(path = "{sourceType}/{sourceId}")
    void createThread(@PathVariable("sourceType") String sourceType,
                      @PathVariable("sourceId") String sourceId,
                      @RequestParam("author") Collection<String> authors) {
        service.createThread(sourceType, sourceId, authors);
    }

    @GetMapping
    CommentThread getThread(String sourceType, String sourceId) {
        return service.getThread(sourceType, sourceId);
    }

    @PostMapping
    String addComment(String sourceType, String sourceId, String text) {
        var author = "user1"; // TODO get from current authenticated user
        return service.addComment(sourceType, sourceId, author, text);
    }

    @PutMapping
    void updateComment(String sourceType, String sourceId, String commentId, String text) {
        service.updateComment(sourceType, sourceId, commentId, text);
    }

    @DeleteMapping
    void deleteComment(String sourceType, String sourceId, String commentId) {
        service.deleteComment(sourceType, sourceId, commentId);
    }
}
