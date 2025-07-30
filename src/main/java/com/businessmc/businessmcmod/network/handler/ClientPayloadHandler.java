package com.businessmc.businessmcmod.network.handler;

import com.businessmc.businessmcmod.client.PlayerClientData;
import com.businessmc.businessmcmod.client.ui.menu.BusinessShopMenu;
import com.businessmc.businessmcmod.client.ui.screen.BusinessShopScreen;
import com.businessmc.businessmcmod.network.payload.ButtonResultPayload;
import com.businessmc.businessmcmod.network.payload.PlayerDataSyncPayload;
import com.businessmc.businessmcmod.network.payload.shopcheckout.ShopCheckoutPayload;
import com.businessmc.businessmcmod.network.payload.shopcheckout.ShopCheckoutResultPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class ClientPayloadHandler {
    public static void handlePlayerDataSync(PlayerDataSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            PlayerClientData.balance = payload.balance();
            PlayerClientData.jobId = payload.jobId();
        });
    }

    public static void handleButtonResult(ButtonResultPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
                    PlayerClientData.jobId = payload.jobId();
                }
        );
    }

    public static void handleShopCheckoutResult(ShopCheckoutResultPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            boolean success = payload.success();
            double balance = payload.balance();

            var mc = Minecraft.getInstance();
            var player = mc.player;

            player.getInventory().setChanged();
            player.inventoryMenu.broadcastChanges();

            // 必要に応じて現在の画面やメニューへ通知可能
            // 例：GUIに直接渡す、あるいは静的データに保持してGUIから参照

            //if (Minecraft.getInstance().screen instanceof BusinessShopScreen screen) {
           //     screen.onShopCheckoutResult(success, balance);
            //} else {
                // Fallback: メッセージ表示など
                Minecraft.getInstance().player.displayClientMessage(
                        Component.literal(success ? "取引成功！ 残高: " + balance : "取引失敗"),
                        false
                );
            //}
        });
    }

}
