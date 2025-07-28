package com.businessmc.businessmcmod.network.handler;

import com.businessmc.businessmcmod.client.PlayerClientData;
import com.businessmc.businessmcmod.network.payload.ButtonResultPayload;
import com.businessmc.businessmcmod.network.payload.PlayerDataSyncPayload;
import net.minecraft.Util;
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
}
