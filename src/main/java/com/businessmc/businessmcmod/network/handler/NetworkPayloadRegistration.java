package com.businessmc.businessmcmod.network.handler;

import com.businessmc.businessmcmod.BusinessMCMod;
import com.businessmc.businessmcmod.network.payload.PlayerDataSyncPayload;
import net.neoforged.bus.api.SubscribeEvent;
import  net.neoforged.fml.common.Mod;
import  net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkPayloadRegistration {

    @SubscribeEvent
    public static void registerServer(RegisterPayloadHandlersEvent event) {
        event.registrar(BusinessMCMod.MOD_ID)
                .playToClient(
                        PlayerDataSyncPayload.TYPE,
                        PlayerDataSyncPayload.STREAM_CODEC,
                        ClientPayloadHandler::handlePlayerDataSync
                );
    }

    @SubscribeEvent
    public static void registerClient(RegisterClientPayloadHandlersEvent event) {
        event.register(
                PlayerDataSyncPayload.TYPE,
                ClientPayloadHandler::handlePlayerDataSync
        );
    }
}