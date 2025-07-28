package com.businessmc.businessmcmod.network.payload;


import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record ButtonResultPayload(Integer jobId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ButtonResultPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("businessmcmod", "button_result"));

    public static final StreamCodec<FriendlyByteBuf, ButtonResultPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, ButtonResultPayload::jobId,
                    ButtonResultPayload::new
            );

    @Override
    public Type<?> type() {
        return TYPE;
    }

    // サーバー→クライアント送信用ヘルパー
    public static void sendToPlayer(ServerPlayer player, Integer jobId) {
        var payload = new ButtonResultPayload(jobId);
        player.connection.send(payload);
    }
}