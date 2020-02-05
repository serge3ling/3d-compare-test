package com.tdc.test;


import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import com.tdc.test.api.Comment;

import com.tdc.test.impl.CommentServiceImpl;
import com.tdc.test.impl.MongoBean;
import de.flapdoodle.embed.mongo.MongodExecutable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.assertj.core.api.Assertions.assertThat;

public class ManualEmbeddedMongoDbIntegrationTest {
    static final String TEST_COLLECTION = "test_collection";

    private MongoBean mongoBean;
    private MongodExecutable executable;
    private MongoTemplate template;

    private com.tdc.test.impl.CommentServiceImpl service;

    @Before
    public void setup() throws Exception {
        mongoBean = new MongoBean();
        mongoBean.postConstruct();
        template = mongoBean.template();
    }

    @After
    public void clean() {
        mongoBean.preDestroy();
    }

    public void addDoc() throws Exception {
        List<Comment> comments = new ArrayList();
        comments.add(new Comment("cid0003", "user3", Instant.now(), Instant.now(), "Hi there!"));
        comments.add(new Comment("cid0004", "user4", Instant.now(), Instant.now(), "Hi!"));

        String testId = "id0002";
        DBObject dbObject = BasicDBObjectBuilder.start()
                .add("_id", testId)
                .add("sourceType", "t2")
                .add("sourceId", "sid0002")
                .add("comments", comments)
                .get();
        template.save(dbObject, TEST_COLLECTION);

        List<DBObject> list = template.findAll(DBObject.class, TEST_COLLECTION);
        assertThat(list).extracting("_id").containsOnly(testId);
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

    @Test
    public void theOne() throws Exception {
        addDoc();

        List<DBObject> comments = new ArrayList();
        String testCid = "cid0002";
        String testText = "Yo!";
        comments.add(makeDbObjectOf(new Comment("cid0001", "user1", Instant.now(), Instant.now(), "Hi there!")));
        comments.add(makeDbObjectOf(new Comment(testCid, "user2", Instant.now(), Instant.now(), testText)));

        String testSrcType = "t1";
        String testSrcId = "sid0001";
        DBObject dbObject = BasicDBObjectBuilder.start()
                .add("_id", "id0001")
                .add("id", "id0001b")
                .add("sourceType", testSrcType)
                .add("sourceId", testSrcId)
                .add("comments", comments)
                .get();
        template.save(dbObject, TEST_COLLECTION);

        Query query = new Query();
        query.addCriteria(Criteria.where("sourceType").is(testSrcType)
                    .andOperator(Criteria.where("sourceId").is(testSrcId))
                );
        List<DBObject> list = template.find(query, DBObject.class, TEST_COLLECTION);
        System.out.println("The list's size is " + list.size() + ".");
        assertThat(list).hasSize(1);

        Document gotBack = (Document) list.get(0);
        List<Comment> commentsBack = (List<Comment>) gotBack.get("comments");
        System.out.println("The comment list's size is " + commentsBack.size() + ".");
        assertThat(commentsBack).hasSameSizeAs(comments);

        Stream stream = commentsBack.stream();
        List<Document> filteredComments = (List) stream.filter(item -> ((Document) item).get("id").equals(testCid))
                .collect(Collectors.toList());
        System.out.println("The comment " + (filteredComments.size() == 1 ? "" : "not ") + "found.");
        assertThat(filteredComments).hasSize(1);
        assertThat(filteredComments.get(0).get("text")).isEqualTo(testText);

        service = new CommentServiceImpl(mongoBean);
        service.setTestRunning(true);
        ArrayList<String> users = new ArrayList<String>();
        users.add("user1");
        users.add("user2");
        service.createThread(testSrcType, testSrcId, users);

        service.addComment(testSrcType, testSrcId, "user1", "I'm testing.");
        String cmtIdCrap = service.addComment(testSrcType, testSrcId, "user2", "Crap.");
        String cmtIdDel = service.addComment(testSrcType, testSrcId, "user1", "Say what?!");
        System.out.println(service.getThread(testSrcType, testSrcId));

        String textToBeLast = "OK.";
        service.updateComment(testSrcType, testSrcId, cmtIdCrap, textToBeLast);
        service.deleteComment(testSrcType, testSrcId, cmtIdDel);
        System.out.println(service.getThread(testSrcType, testSrcId));

        /*
        Two users talking:
        user1: I'm testing.
        user2: Crap.
        user1: Say what?!
        Then user2 changes their comment to OK and user1 deletes their last comment.
        Then the last comment in the thread should be:
        user2: OK.
        */
        List<Comment> commentList = service.getThread(testSrcType, testSrcId).getComments();
        assertThat(commentList.get(commentList.size() - 1).getText()).isEqualTo(textToBeLast);
    }
}
