package com.businessmc.businessmcmod.util.config;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BusinessMCCommonConfig {
    public static final ModConfigSpec SPEC;

    // クライアント接続設定
    public static final ModConfigSpec.ConfigValue<String> CLIENT_MONGO_HOST_PRIMARY;
    public static final ModConfigSpec.IntValue CLIENT_MONGO_PORT_PRIMARY;
    public static final ModConfigSpec.ConfigValue<String> CLIENT_MONGO_HOST_FALLBACK;
    public static final ModConfigSpec.IntValue CLIENT_MONGO_PORT_FALLBACK;

    // サーバー接続設定
    public static final ModConfigSpec.ConfigValue<String> SERVER_MONGO_HOST_PRIMARY;
    public static final ModConfigSpec.IntValue SERVER_MONGO_PORT_PRIMARY;
    public static final ModConfigSpec.ConfigValue<String> SERVER_MONGO_HOST_SECONDARY;
    public static final ModConfigSpec.IntValue SERVER_MONGO_PORT_SECONDARY;
    public static final ModConfigSpec.ConfigValue<String> SERVER_MONGO_HOST_FALLBACK;
    public static final ModConfigSpec.IntValue SERVER_MONGO_PORT_FALLBACK;

    // 認証情報と接続設定
    public static final ModConfigSpec.ConfigValue<String> MONGO_USER;
    public static final ModConfigSpec.ConfigValue<String> MONGO_PASSWORD;
    public static final ModConfigSpec.ConfigValue<String> MONGO_AUTH_DB;
    public static final ModConfigSpec.IntValue MONGO_MAX_POOL_SIZE;
    public static final ModConfigSpec.IntValue MONGO_MIN_POOL_SIZE;
    public static final ModConfigSpec.IntValue MONGO_CONNECT_TIMEOUT_MS;
    public static final ModConfigSpec.IntValue MONGO_SOCKET_TIMEOUT_MS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("MongoDB Configuration for BusinessMCMod");

        // クライアント設定
        builder.push("client_mongo");
        CLIENT_MONGO_HOST_PRIMARY = builder
                .comment("Primary MongoDB host for client")
                .define("client_mongo_host_primary", "〇.〇.〇.〇");
        CLIENT_MONGO_PORT_PRIMARY = builder
                .comment("Primary MongoDB port for client")
                .defineInRange("client_mongo_port_primary", 27117, 1, 65535);
        CLIENT_MONGO_HOST_FALLBACK = builder
                .comment("Fallback MongoDB host for client")
                .define("client_mongo_host_fallback", "127.0.0.1");
        CLIENT_MONGO_PORT_FALLBACK = builder
                .comment("Fallback MongoDB port for client")
                .defineInRange("client_mongo_port_fallback", 27017, 1, 65535);
        builder.pop();

        // サーバー設定
        builder.push("server_mongo");
        SERVER_MONGO_HOST_PRIMARY = builder
                .comment("Primary MongoDB host for server (Docker: mongo)")
                .define("server_mongo_host_primary", "mongo");
        SERVER_MONGO_PORT_PRIMARY = builder
                .comment("Primary MongoDB port for server")
                .defineInRange("server_mongo_port_primary", 27017, 1, 65535);
        SERVER_MONGO_HOST_SECONDARY = builder
                .comment("Secondary MongoDB host for server")
                .define("server_mongo_host_secondary", "〇.〇.〇.〇");
        SERVER_MONGO_PORT_SECONDARY = builder
                .comment("Secondary MongoDB port for server")
                .defineInRange("server_mongo_port_secondary", 27117, 1, 65535);
        SERVER_MONGO_HOST_FALLBACK = builder
                .comment("Fallback MongoDB host for server")
                .define("server_mongo_host_fallback", "127.0.0.1");
        SERVER_MONGO_PORT_FALLBACK = builder
                .comment("Fallback MongoDB port for server")
                .defineInRange("server_mongo_port_fallback", 27017, 1, 65535);
        builder.pop();

        // 認証情報と接続設定
        builder.push("mongo_settings");
        MONGO_USER = builder
                .comment("MongoDB username")
                .define("mongo_user", "admin");
        MONGO_PASSWORD = builder
                .comment("MongoDB password")
                .define("mongo_password", "secret");
        MONGO_AUTH_DB = builder
                .comment("MongoDB authentication database")
                .define("mongo_auth_db", "admin");
        MONGO_MAX_POOL_SIZE = builder
                .comment("Maximum connection pool size")
                .defineInRange("mongo_max_pool_size", 100, 1, 1000);
        MONGO_MIN_POOL_SIZE = builder
                .comment("Minimum connection pool size")
                .defineInRange("mongo_min_pool_size", 10, 0, 100);
        MONGO_CONNECT_TIMEOUT_MS = builder
                .comment("Connection timeout in milliseconds")
                .defineInRange("mongo_connect_timeout_ms", 3000, 1000, 30000);
        MONGO_SOCKET_TIMEOUT_MS = builder
                .comment("Socket timeout in milliseconds")
                .defineInRange("mongo_socket_timeout_ms", 3000, 1000, 30000);
        builder.pop();

        SPEC = builder.build();
    }

    public static void register() {
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, SPEC, "businessmcmod-common.toml");
    }
}