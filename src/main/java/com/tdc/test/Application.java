package com.tdc.test;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws IOException {
        String ip = "localhost";
        int port = 27017;

        var config = new MongodConfigBuilder()
                .version(Version.Main.DEVELOPMENT)
                .net(new Net(ip, port, Network.localhostIsIPv6())).build();

        var starter = MongodStarter.getDefaultInstance();
        var executable = starter.prepare(config);
        executable.start();

        return new MongoTemplate(new MongoClient(ip, port), "test");
    }
}
