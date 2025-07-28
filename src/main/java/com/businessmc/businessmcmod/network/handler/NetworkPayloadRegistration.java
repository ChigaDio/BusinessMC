package com.businessmc.businessmcmod.network.handler;

import com.businessmc.businessmcmod.BusinessMCMod;
import com.businessmc.businessmcmod.network.payload.ButtonClickPayload;
import com.businessmc.businessmcmod.network.payload.ButtonResultPayload;
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
                )
                .playToServer(
                        ButtonClickPayload.TYPE,
                        ButtonClickPayload.STREAM_CODEC,
                        ServerPayloadHandler::handleButtonClick
                )

                // server -> client: ボタン処理結果
                .playToClient(
                        ButtonResultPayload.TYPE,
                        ButtonResultPayload.STREAM_CODEC,
                        ClientPayloadHandler::handleButtonResult
                );
    }

    @SubscribeEvent
    public static void registerClient(RegisterClientPayloadHandlersEvent event) {
        event.register(
                PlayerDataSyncPayload.TYPE,
                ClientPayloadHandler::handlePlayerDataSync
        );
        event.register(
                ButtonResultPayload.TYPE,
                ClientPayloadHandler::handleButtonResult
        );
    }
}