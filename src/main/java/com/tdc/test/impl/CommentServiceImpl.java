package com.tdc.test.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.tdc.test.api.Comment;
import com.tdc.test.api.CommentService;
import com.tdc.test.api.CommentThread;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CommentServiceImpl implements CommentService {
    public static final String THREADS = "comment_threads";
    public static final String TEXTS = "comment_thread_texts";

    private final CommentThreadRepository repository;

    @Autowired
    private MongoBean mongoBean;

    public CommentServiceImpl() {
        repository = new CommentThreadRepository() {
            @Override
            public Optional<CommentThreadEntity> findBySourceTypeAndSourceId(String sourceType, String sourceId) {
                Optional<CommentThreadEntity> oc = Optional.empty();

                /*Query query = new Query();
                query.addCriteria(Criteria.where("sourceType").is(sourceType)
                        .andOperator(Criteria.where("sourceId").is(sourceId))
                );
                List<DBObject> list = mongoBean.template().find(query, DBObject.class, THREADS);

                if (list.size() == 1) {
                    Document doc = (Document) list.get(0);
                    CommentThreadEntity cmtThreadEnt = new CommentThreadEntity(
                            (String) doc.get("id"),
                            (String) doc.get("sourceType"), (String) doc.get("sourceId"));
                    oc = Optional.of(cmtThreadEnt);
                }*/
                Query query = new Query();
                query.addCriteria(Criteria.where("sourceType").is(sourceType)
                    .andOperator(Criteria.where("sourceId").is(sourceId))
                );
                List<CommentThreadEntity> entities = mongoBean.template().find(query, CommentThreadEntity.class, THREADS);
                System.out.println("entities.size(): " + entities.size() + ".");
                if (entities.size() == 1) {
                    oc = Optional.of(entities.get(0));
                }

                return oc;
            }
        };
    }

    public CommentServiceImpl(CommentThreadRepository repository) {
        this.repository = repository;
    }

    // for testing purposes
    public CommentServiceImpl(MongoBean mongoBean) {
        this();
        this.mongoBean = mongoBean;
    }

    private DBObject makeDbObjectOf(Comment comment) {
        return BasicDBObjectBuilder.start()
                .add("id", comment.getId())
                .add("author", comment.getAuthor())
                .add("createdAt", comment.getCreatedAt())
                .add("updatedAt", comment.getUpdatedAt())
                .add("text", comment.getText())
                .get();
    }

    private void updateAuthors(String link, Collection<String> authors) {
        Query query = new Query();
        query.addCriteria(Criteria.where("link").is(link));
        List<CommentThread> texts = mongoBean.template().find(query, CommentThread.class, TEXTS);
        CommentThread cmtThread = texts.get(0);//cmtThread.setAuthors(authors);

        Document doc = new Document();
        mongoBean.template().getConverter().write(cmtThread, doc);
        Update update = Update.fromDocument(doc);

        mongoBean.template().upsert(query, update, TEXTS);
    }

    private Collection<String> pullAuthors(String link) {
        Query query = new Query();
        query.addCriteria(Criteria.where("link").is(link));
        List<CommentThread> texts = mongoBean.template().find(query, CommentThread.class, TEXTS);
        CommentThread cmtThread = texts.get(0);
        return cmtThread.getAuthors();
    }

    @Override
    public void createThread(String sourceType, String sourceId, Collection<String> authors) {
        Optional<CommentThreadEntity> opt = repository.findBySourceTypeAndSourceId(sourceType, sourceId);
        System.out.println("opt.isEmpty(): " + opt.isEmpty() + ".");
        String link = opt.isEmpty() ? UUID.randomUUID().toString() : opt.get().getId();

        if (opt.isEmpty()) {
            CommentThreadEntity cmtThreadEnt = new CommentThreadEntity(link, sourceType, sourceId);
            CommentThread cmtTread = new CommentThread(link, authors, new java.util.ArrayList<Comment>());
            mongoBean.template().save(cmtThreadEnt, THREADS);
            mongoBean.template().save(cmtTread, TEXTS);
        }
    }

    @Override
    public CommentThread getThread(String sourceType, String sourceId) {
        CommentThread oc = null;
        Optional<CommentThreadEntity> opt = repository.findBySourceTypeAndSourceId(sourceType, sourceId);

        if (opt.isPresent()) {
            String link = opt.get().getId();

            Query query = new Query();
            query.addCriteria(Criteria.where("link").is(link));
            List<CommentThread> commentThreads = mongoBean.template().find(query, CommentThread.class, TEXTS);
            oc = commentThreads.get(0);
        }

        return oc;
    }

    @Override
    public String addComment(String sourceType, String sourceId, String author, String text) {
        String commentId = UUID.randomUUID().toString();
        Optional<CommentThreadEntity> opt = repository.findBySourceTypeAndSourceId(sourceType, sourceId);

        if (opt.isPresent()) {
            String link = opt.get().getId();

            Query query = new Query();
            query.addCriteria(Criteria.where("link").is(link));
            List<CommentThread> commentThreads = mongoBean.template().find(query, CommentThread.class, TEXTS);

            CommentThread commentThread = commentThreads.get(0);
            Collection<String> authors = commentThread.getAuthors();
            System.out.println("addComment, commentThread, authors: " + authors);

            if (authors.contains(author)) {
                List<Comment> comments = commentThread.getComments();
                comments.add(new Comment(commentId, author, Instant.now(), Instant.now(), text));

                CommentThread commentThreadNew = new CommentThread(link, authors, comments);
                Document doc = new Document();
                mongoBean.template().getConverter().write(commentThreadNew, doc);
                Update update = Update.fromDocument(doc);

                mongoBean.template().upsert(query, update, TEXTS);
            }
        }

        return commentId;
    }

    @Override
    public void updateComment(String sourceType, String sourceId, String commentId, String text) {
        // TODO
        Optional<CommentThreadEntity> opt = repository.findBySourceTypeAndSourceId(sourceType, sourceId);

        if (opt.isPresent()) {
            String link = opt.get().getId();

            Query query = new Query();
            query.addCriteria(Criteria.where("link").is(link));
            List<CommentThread> commentThreads = mongoBean.template().find(query, CommentThread.class, TEXTS);

            CommentThread commentThread = commentThreads.get(0);
            Collection<String> authors = commentThread.getAuthors();
            System.out.println("addComment, commentThread, authors: " + authors);

            if (true/*authors.contains(author)*/) {
                List<Comment> comments = commentThread.getComments();
                List<Comment> commentsNew = comments.stream().map(
                        cmt ->
                        (cmt.getId().equals(commentId) ?
                        new Comment(commentId, cmt.getAuthor(), cmt.getCreatedAt(), Instant.now(), text) :
                        cmt)
                ).collect(Collectors.toList());

                CommentThread commentThreadNew = new CommentThread(link, authors, commentsNew);
                Document doc = new Document();
                mongoBean.template().getConverter().write(commentThreadNew, doc);
                Update update = Update.fromDocument(doc);

                mongoBean.template().upsert(query, update, TEXTS);
            }
        }
    }

    @Override
    public void deleteComment(String sourceType, String sourceId, String commentId) {
        // TODO
        Optional<CommentThreadEntity> opt = repository.findBySourceTypeAndSourceId(sourceType, sourceId);

        if (opt.isPresent()) {
            String link = opt.get().getId();

            Query query = new Query();
            query.addCriteria(Criteria.where("link").is(link));
            List<CommentThread> commentThreads = mongoBean.template().find(query, CommentThread.class, TEXTS);

            CommentThread commentThread = commentThreads.get(0);
            Collection<String> authors = commentThread.getAuthors();
            System.out.println("addComment, commentThread, authors: " + authors);

            if (true/*authors.contains(author)*/) {
                List<Comment> comments = commentThread.getComments();
                boolean found = false;

                for (int i = 0; i < comments.size(); i++) {
                    if (comments.get(i).getId().equals(commentId)) {
                        found = true;
                        comments.remove(i);
                        break;
                    }
                }

                if (found) {
                    CommentThread commentThreadNew = new CommentThread(link, authors, comments);
                    Document doc = new Document();
                    mongoBean.template().getConverter().write(commentThreadNew, doc);
                    Update update = Update.fromDocument(doc);

                    mongoBean.template().upsert(query, update, TEXTS);
                }
            }
        }
    }
}
