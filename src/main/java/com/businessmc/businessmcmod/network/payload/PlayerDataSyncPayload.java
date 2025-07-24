package com.businessmc.businessmcmod.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;


public record PlayerDataSyncPayload(String playerId, double balance, int jobId) implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<PlayerDataSyncPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("businessmcmod", "player_data_sync"));

    public static final StreamCodec<ByteBuf, PlayerDataSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PlayerDataSyncPayload::playerId,
            ByteBufCodecs.DOUBLE,
            PlayerDataSyncPayload::balance,
            ByteBufCodecs.VAR_INT,
            PlayerDataSyncPayload::jobId,
            PlayerDataSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendTo(ServerPlayer player, double balance, int jobId) {
        player.connection.send(new ClientboundCustomPayloadPacket(
                new PlayerDataSyncPayload(player.getStringUUID(), balance, jobId)
        ));
    }

}
