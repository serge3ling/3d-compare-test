package com.tdc.test.rest;

import com.tdc.test.api.CommentService;
import com.tdc.test.api.CommentThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("comments")
class CommentController {
    @Autowired
    private final CommentService service;

    CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping(path = "{sourceType}/{sourceId}")
    void createThread(@PathVariable("sourceType") String sourceType,
                      @PathVariable("sourceId") String sourceId,
                      @RequestParam("authors") Collection<String> authors) {
        service.createThread(sourceType, sourceId, authors);
    }
    // POST: http://localhost:8080/comments/t1/s1?authors=user1,user2

    @GetMapping
    String getWelcome() {
        String pretty = "<style>body {color: #7c7; background-color: #355; font-family: sans;}</style>" +
                "<body><h3>Welcome!</h3></body>";
        return pretty;
    }
    // GET: http://localhost:8080/comments

    @GetMapping(path = "{sourceType}/{sourceId}")
    CommentThread getThread(@PathVariable("sourceType") String sourceType,
                            @PathVariable("sourceId") String sourceId) {
        return service.getThread(sourceType, sourceId);
    }
    // GET: http://localhost:8080/comments/t1/s1

    @PostMapping(path = "{sourceType}/{sourceId}/add")
    String addComment(@PathVariable("sourceType") String sourceType,
                      @PathVariable("sourceId") String sourceId,
                      @RequestBody String text) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        author = (author == null) ? ("") : author;
        return service.addComment(sourceType, sourceId, author, text);
    }
    // POST: http://localhost:8080/comments/t1/s1/add (request body is the text)

    @PutMapping(path = "{sourceType}/{sourceId}/{commentId}")
    void updateComment(@PathVariable("sourceType") String sourceType,
                       @PathVariable("sourceId") String sourceId,
                       @PathVariable("commentId") String commentId,
                       @RequestBody String text) {
        service.updateComment(sourceType, sourceId, commentId, text);
    }
    // PUT: http://localhost:8080/comments/t1/s1/33346522-a0e9-4fe1-ab75-896bf1956ca0 (request body is the text)

    @DeleteMapping(path = "{sourceType}/{sourceId}/{commentId}")
    void deleteComment(@PathVariable("sourceType") String sourceType,
                       @PathVariable("sourceId") String sourceId,
                       @PathVariable("commentId") String commentId) {
        service.deleteComment(sourceType, sourceId, commentId);
    }
    // DELETE: http://localhost:8080/comments/t1/s1/21c9a7bb-e49e-4323-896e-0ed406618215
}
