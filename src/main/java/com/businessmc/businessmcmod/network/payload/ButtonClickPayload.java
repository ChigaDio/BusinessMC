package com.businessmc.businessmcmod.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public record ButtonClickPayload(int buttonIndex, int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ButtonClickPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("businessmcmod", "button_click"));

    public static final StreamCodec<FriendlyByteBuf, ButtonClickPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,               // buttonIndex
                    ButtonClickPayload::buttonIndex,
                    ByteBufCodecs.VAR_INT,               // entityId
                    ButtonClickPayload::entityId,
                    ButtonClickPayload::new              // デコード時コンストラクタ参照
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToServer(int buttonIndex, int entityId) {
        // NeoForge 標準の PacketDistributor を使う
        ClientPacketDistributor.sendToServer( new ButtonClickPayload(buttonIndex, entityId));
    }
}