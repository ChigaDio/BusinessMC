package com.businessmc.businessmcmod;

import com.businessmc.businessmcmod.util.collection.UserGamePlayerCollectionData;
import com.businessmc.businessmcmod.util.collection.UserGamePlayerCollectionDb;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import org.bson.Document;

import java.util.ArrayList;

@Mod(BusinessMCMod.MOD_ID)
public class BusinessMCMod {
    public static final String MOD_ID = "businessmcmod";

    private static MongoClient mongoClient;
    private static MongoDatabase gameDatabase;

    // NeoForge 21+ に対応：正しいコンストラクタ（modEventBusとmodContainerを受け取る）
    public BusinessMCMod(IEventBus modEventBus, ModContainer modContainer) {
        System.out.println("BusinessMCMod: Constructing mod");

        // ライフサイクルイベント（例：common setup）など登録
        modEventBus.addListener(this::onCommonSetup);

        // プレイヤーログインやサーバー起動イベントは NeoForge のグローバルバスに登録
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        System.out.println("BusinessMCMod: onCommonSetup");
        // 現時点で設定を参照したりしないなら空でもOK
    }

    private void onServerStarting(ServerStartingEvent event) {
        System.out.println("BusinessMCMod: Server starting");

        try {
            // MongoDB 接続（シングルトン）
            //String connectionString = "mongodb://admin:secret@127.0.0.1:27017/?authSource=admin";
            //mongoClient = MongoClients.create(connectionString);
//
            //MongoDatabase adminDb = mongoClient.getDatabase("admin");
            //Document ping = adminDb.runCommand(new Document("ping", 1));
            //System.out.println("MongoDB ping: " + ping.toJson());
//
            //gameDatabase = mongoClient.getDatabase("game");
//
            //event.getServer().getCommands().performPrefixedCommand(
            //        event.getServer().createCommandSourceStack(),
            //        "MongoDB Success"
            //);
        } catch (Exception e) {
            System.err.println("MongoDB connection failed: " + e.getMessage());
            event.getServer().getCommands().performPrefixedCommand(
                    event.getServer().createCommandSourceStack(),
                    "MongoDB Connection Failed"
            );
            e.printStackTrace();
        }
    }

    private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        String connectionString = "mongodb://admin:secret@127.0.0.1:27017/?authSource=admin";
        mongoClient = MongoClients.create(connectionString);
        gameDatabase = mongoClient.getDatabase("game");
        String uuid = player.getUUID().toString();
        String name = player.getName().getString();

        if (mongoClient == null || gameDatabase == null) {
            System.err.println("MongoDB not initialized");
            return;
        }

        try (ClientSession session = mongoClient.startSession())
        {
            session.startTransaction();
            try {
                var playerData = UserGamePlayerCollectionDb.findPlayerGameDataOne(gameDatabase, uuid);
                if (playerData.getResult() == null) {
                    UserGamePlayerCollectionData data = new UserGamePlayerCollectionData();
                    data.setPlayer_id(uuid);
                    data.setPlayer_name(name);

                    UserGamePlayerCollectionDb.bulkInsertUserGamePlayer(gameDatabase,
                            new ArrayList<>() {{ add(data); }});

                    session.commitTransaction();
                    System.out.println("New player registered: " + name);
                }
            } catch (Exception e) {
                session.abortTransaction();
                System.err.println("Player insert failed: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("MongoDB session failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
