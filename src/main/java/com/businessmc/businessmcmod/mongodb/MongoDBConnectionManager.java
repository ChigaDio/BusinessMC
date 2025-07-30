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


import com.businessmc.businessmcmod.util.config.BusinessMCCommonConfig;

import com.mongodb.MongoException;


import net.neoforged.fml.loading.FMLEnvironment;
import org.bson.Document;


public class MongoDBConnectionManager {
    private static MongoClient mongoClient;

    private MongoDBConnectionManager() {
    }

    public static MongoClient getInstance() {
        if (mongoClient == null) {
            if (FMLEnvironment.dist.isClient()) {
                mongoClient = tryConnectClient();
            } else {
                mongoClient = tryConnectServer();
            }
        }
        return mongoClient;
    }

    private static MongoClient tryConnectClient() {
        String user = BusinessMCCommonConfig.MONGO_USER.get();
        String password = BusinessMCCommonConfig.MONGO_PASSWORD.get();
        String authDb = BusinessMCCommonConfig.MONGO_AUTH_DB.get();
        int maxPoolSize = BusinessMCCommonConfig.MONGO_MAX_POOL_SIZE.get();
        int minPoolSize = BusinessMCCommonConfig.MONGO_MIN_POOL_SIZE.get();
        int connectTimeoutMs = BusinessMCCommonConfig.MONGO_CONNECT_TIMEOUT_MS.get();
        int socketTimeoutMs = BusinessMCCommonConfig.MONGO_SOCKET_TIMEOUT_MS.get();

        String[] hosts = {
                BusinessMCCommonConfig.CLIENT_MONGO_HOST_PRIMARY.get(),
                BusinessMCCommonConfig.CLIENT_MONGO_HOST_FALLBACK.get()
        };
        int[] ports = {
                BusinessMCCommonConfig.CLIENT_MONGO_PORT_PRIMARY.get(),
                BusinessMCCommonConfig.CLIENT_MONGO_PORT_FALLBACK.get()
        };

        for (int i = 0; i < hosts.length; i++) {
            // ループ内の変数をローカル変数にコピー
            final String currentHost = hosts[i];
            final int currentPort = ports[i];

            try {
                MongoCredential credential = MongoCredential.createCredential(user, authDb, password.toCharArray());

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
                                builder.hosts(Collections.singletonList(new ServerAddress(currentHost, currentPort))))
                        .credential(credential)
                        .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                        .applyToSocketSettings(builder -> builder.applySettings(socketSettings))
                        .build();

                MongoClient client = MongoClients.create(settings);
                // 接続テスト
                client.getDatabase(authDb).runCommand(new Document("ping", 1));
                System.out.println("Successfully connected to MongoDB at " + currentHost + ":" + currentPort);
                return client;
            } catch (MongoException e) {
                System.err.println("Failed to connect to MongoDB at " + currentHost + ":" + currentPort + ": " + e.getMessage());
            }
        }
        throw new RuntimeException("Failed to connect to any MongoDB instance for client");
    }

    private static MongoClient tryConnectServer() {
        String user = BusinessMCCommonConfig.MONGO_USER.get();
        String password = BusinessMCCommonConfig.MONGO_PASSWORD.get();
        String authDb = BusinessMCCommonConfig.MONGO_AUTH_DB.get();
        int maxPoolSize = BusinessMCCommonConfig.MONGO_MAX_POOL_SIZE.get();
        int minPoolSize = BusinessMCCommonConfig.MONGO_MIN_POOL_SIZE.get();
        int connectTimeoutMs = BusinessMCCommonConfig.MONGO_CONNECT_TIMEOUT_MS.get();
        int socketTimeoutMs = BusinessMCCommonConfig.MONGO_SOCKET_TIMEOUT_MS.get();

        String[] hosts = {
                BusinessMCCommonConfig.SERVER_MONGO_HOST_PRIMARY.get(),
                BusinessMCCommonConfig.SERVER_MONGO_HOST_SECONDARY.get(),
                BusinessMCCommonConfig.SERVER_MONGO_HOST_FALLBACK.get()
        };
        int[] ports = {
                BusinessMCCommonConfig.SERVER_MONGO_PORT_PRIMARY.get(),
                BusinessMCCommonConfig.SERVER_MONGO_PORT_SECONDARY.get(),
                BusinessMCCommonConfig.SERVER_MONGO_PORT_FALLBACK.get()
        };

        for (int i = 0; i < hosts.length; i++) {
            // ループ内の変数をローカル変数にコピー
            final String currentHost = hosts[i];
            final int currentPort = ports[i];

            try {
                MongoCredential credential = MongoCredential.createCredential(user, authDb, password.toCharArray());

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
                                builder.hosts(Collections.singletonList(new ServerAddress(currentHost, currentPort))))
                        .credential(credential)
                        .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                        .applyToSocketSettings(builder -> builder.applySettings(socketSettings))
                        .build();

                MongoClient client = MongoClients.create(settings);
                // 接続テスト
                client.getDatabase(authDb).runCommand(new Document("ping", 1));
                System.out.println("Successfully connected to MongoDB at " + currentHost + ":" + currentPort);
                return client;
            } catch (MongoException e) {
                System.err.println("Failed to connect to MongoDB at " + currentHost + ":" + currentPort + ": " + e.getMessage());
            }
        }
        throw new RuntimeException("Failed to connect to any MongoDB instance for server");
    }

    public static synchronized void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}