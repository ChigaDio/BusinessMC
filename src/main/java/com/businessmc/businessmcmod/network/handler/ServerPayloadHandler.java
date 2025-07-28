package com.businessmc.businessmcmod.network.handler;

import com.businessmc.businessmcmod.entity.belmond.BelmondEntity;
import com.businessmc.businessmcmod.network.payload.ButtonClickPayload;
import net.minecraft.network.chat.Component;
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
}