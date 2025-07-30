package com.businessmc.businessmcmod.network.handler;

import com.businessmc.businessmcmod.entity.belmond.BelmondEntity;
import com.businessmc.businessmcmod.mongodb.MongoDBConnectionManager;
import com.businessmc.businessmcmod.network.payload.ButtonClickPayload;
import com.businessmc.businessmcmod.network.payload.PlayerDataSyncPayload;
import com.businessmc.businessmcmod.network.payload.shopcheckout.ShopCheckoutResultPayload;
import com.businessmc.businessmcmod.util.client.UtilClient;
import com.businessmc.businessmcmod.util.collection.BusinessShopCollectionDb;
import com.businessmc.businessmcmod.util.collection.UserGamePlayerCollectionDb;
import com.businessmc.businessmcmod.util.collection.details.ShopItem;
import com.businessmc.businessmcmod.util.collection.transaction.TransactionExecutor;
import com.businessmc.businessmcmod.util.collection.transaction.TransactionResult;
import com.businessmc.businessmcmod.util.general.UtilGeneral;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class ServerPayloadHandler {
    public static void handleButtonClick(com.businessmc.businessmcmod.network.payload.ButtonClickPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;


            Entity e = player.level().getEntity(payload.entityId());
            if (e instanceof BelmondEntity belmond) {
                belmond.handleButtonClick(player, payload.buttonIndex());
            } else {
                player.sendSystemMessage(
                        Component.literal("Entity not found: " + payload.entityId())
                );
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public static void handleShopCheckout(com.businessmc.businessmcmod.network.payload.shopcheckout.ShopCheckoutPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            int shopId = payload.ShopID();
            String itemName = payload.ItemName();
            double amount = payload.Amout();
            String playerID = payload.PlayerId();
            boolean buyFlag = payload.BuyFlag();
            int itemCount = payload.ItemCount();
            // ここで購入処理を行う（例：DBにアクセスやインベントリ確認など）
            boolean success = true; // 仮の成功判定


            var player_data = UserGamePlayerCollectionDb.findPlayerGameDataOne(MongoDBConnectionManager.getInstance().getDatabase("game"),playerID);
            if(!player_data.isSuccess()) {
                return;
            }
            double newBalance = player_data.getResult().getBalance(); // 仮の新残高


            var transaction = new TransactionExecutor(MongoDBConnectionManager.getInstance());
            var transaction_result = transaction.runTransaction("game", (db, session) -> {

                var result_flag = false;



                var shop_data_result = BusinessShopCollectionDb.findIdOne(db,session,shopId);
                if(!shop_data_result.isSuccess())
                {
                    return TransactionResult.failure("No Shop:" + shopId);
                }

                var shop_data = shop_data_result.getResult();

                var shop_item =  buyFlag ? shop_data.getBuyList().stream()
                                .filter(item -> item.getItemIdName().equals(itemName)).findFirst() :
                                shop_data.getSaleList().stream()
                                .filter(item -> item.getItemIdName().equals(itemName)).findFirst();
                if(shop_item.isEmpty())
                {
                    return TransactionResult.failure("No ShopItem:" + itemName);
                }
                var item = UtilGeneral.getItemFromName(itemName);
                if(item == null) return TransactionResult.failure("存在しないアイテムです:" + itemName);
                var balance = player_data.getResult().getBalance();
                var fix = (shop_data.getBaseRate() * (shop_item.get().getPriceRate() * shop_item.get().getPrice())) * itemCount;


                //売る、買うのフラグ計算
                if(buyFlag)
                {
                    //買う処理
                    //所持金計算
                    balance -= fix;
                    if(balance <= 0.0 || fix != amount)
                    {
                        return TransactionResult.failure("所持金以上の金額です:" + shopId);
                    }
                    var update_result = UserGamePlayerCollectionDb.addBalance(db,session,playerID,(-fix),true);
                    if(!update_result.isSuccess()) return TransactionResult.failure("所持金更新のDBでエラーです:"+ playerID);

                    var result = UtilClient.addItemToPlayer(player,itemName,itemCount);
                    if(!result) return TransactionResult.failure("アイテム購入エラーです:"+ playerID);

                }
                else
                {
                    var update_result = UserGamePlayerCollectionDb.addBalance(db,session,playerID,(fix),true);
                    if(!update_result.isSuccess()) return TransactionResult.failure("所持金更新のDBでエラーです:"+ playerID);
                    var result =  UtilClient.sellItemFromPlayer(player,itemName,itemCount);
                    if(!result) return TransactionResult.failure("個数エラーです:"+ playerID);
                    balance += fix;

                }


                ShopCheckoutResultPayload.sendToClient(player, true, balance);
                PlayerDataSyncPayload.sendTo(player,balance,player_data.getResult().getJobId());
                return TransactionResult.flagReturn(true, "User inserted.");
            }
            );


        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

}