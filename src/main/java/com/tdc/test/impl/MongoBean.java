package com.tdc.test.impl;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6;

@Component
public class MongoBean {
    private MongodExecutable executable;
    private MongoTemplate mongoTemplate;

    public MongoTemplate template() {
        return mongoTemplate;
    }

    @PostConstruct
    public void postConstruct() throws Exception {
        String ip = "localhost";
        int port = 27019;

        IMongodConfig config = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(ip, port, localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        executable = starter.prepare(config);
        executable.start();
        mongoTemplate = new MongoTemplate(new MongoClient(ip, port), "test");
    }

    @PreDestroy
    public void preDestroy() {
        if (executable != null) {
            executable.stop();
        }
    }
}
