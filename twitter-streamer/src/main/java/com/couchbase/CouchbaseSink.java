package com.couchbase;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.*;
import com.couchbase.client.java.document.json.*;
import com.couchbase.client.java.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CouchbaseSink implements TweetSink {


    private Cluster cluster;
    private Bucket tweetBucket;


    @Value("#{environment['COUCHBASE_CLUSTER']}")
    private String couchbaseAddress;

    @Value("#{environment['COUCHBASE_USER']}")
    private String couchbaseUserName;

    @Value("#{environment['COUCHBASE_PASSWORD']}")
    private String couchbasePassword;

    @Value("#{environment['COUCHBASE_TWEET_BUCKET']}")
    private String bucketName;

    @Autowired
    public CouchbaseSink() {

    }


    @Override
    @PostConstruct
    public void connect() {
        cluster = CouchbaseCluster.create(couchbaseAddress);
        cluster.authenticate(couchbaseUserName, couchbasePassword);
        tweetBucket = cluster.openBucket(bucketName);

        JsonObject test = JsonObject.create()
                .put("key1", "val1")
                .put("key2", "val2");


        tweetBucket.upsert(JsonDocument.create("u:test", test));

    }

    public void addTweet(String msg) {
        JsonObject tweet = JsonObject.fromJson(msg);
        if (tweet.containsKey("id")) {
            tweetBucket.upsert(JsonDocument.create(tweet.get("id").toString(), tweet));
        }
    }




}
