package com.businessmc.businessmcmod.mongodb;


import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SocketSettings;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MongoDBConnectionManager {

    private static MongoClient mongoClient;

    private MongoDBConnectionManager() {
    }

    public  static MongoClient getInstance()
    {
        return mongoClient;
    }

    public static synchronized MongoClient getInstance(
            String host,
            int port,
            String username,
            String password,
            String authSource,
            int maxPoolSize,
            int minPoolSize,
            int connectTimeoutMs,
            int socketTimeoutMs
    ) {
        if (mongoClient == null) {
            MongoCredential credential = MongoCredential.createCredential(username, authSource, password.toCharArray());

            ConnectionPoolSettings poolSettings = ConnectionPoolSettings.builder()
                    .maxSize(maxPoolSize)
                    .minSize(minPoolSize)
                    .maxConnectionIdleTime(60, TimeUnit.SECONDS)
                    .maxWaitTime(30, TimeUnit.SECONDS)
                    .build();

            SocketSettings socketSettings = SocketSettings.builder()
                    .connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
                    .readTimeout(socketTimeoutMs, TimeUnit.MILLISECONDS)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyToClusterSettings(builder ->
                            builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                    .credential(credential)
                    .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                    .applyToSocketSettings(builder -> builder.applySettings(socketSettings))
                    .build();

            mongoClient = MongoClients.create(settings);
        }
        return mongoClient;
    }

    public static synchronized void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}
