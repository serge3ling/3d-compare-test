package com.tdc.test.api;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Null;
import java.util.Collection;
import java.util.List;

@Data
public class CommentThread {
    @NonNull private final String link;
    @NonNull private final Collection<String> authors;
    @NonNull private final List<Comment> comments;

    /*public CommentThread(@NonNull String link, @NonNull Collection<String> authors, @NonNull List<Comment> comments) {
        this.link = link;
        this.authors = authors;
        this.comments = comments;
    }

    public Collection<String> getAuthors() {
        return authors;
    }

    public List<Comment> getComments() {
        return comments;
    }*/
}
