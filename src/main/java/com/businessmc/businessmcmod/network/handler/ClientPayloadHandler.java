package com.businessmc.businessmcmod.network.handler;

import com.businessmc.businessmcmod.client.PlayerClientData;
import com.businessmc.businessmcmod.network.payload.PlayerDataSyncPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class ClientPayloadHandler {
    public static void handlePlayerDataSync(PlayerDataSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            PlayerClientData.balance = payload.balance();
            PlayerClientData.jobId = payload.jobId();
        });
    }
}
