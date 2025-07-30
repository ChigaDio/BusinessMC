package com.businessmc.businessmcmod.network.payload.shopcheckout;


import com.businessmc.businessmcmod.BusinessMCMod;
import com.businessmc.businessmcmod.network.payload.ButtonClickPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;

public record ShopCheckoutResultPayload(boolean success, double balance) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ShopCheckoutResultPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BusinessMCMod.MOD_ID,
                    "checkout_result"));

    public static final StreamCodec<FriendlyByteBuf, ShopCheckoutResultPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ShopCheckoutResultPayload::success,
                    ByteBufCodecs.DOUBLE, ShopCheckoutResultPayload::balance,
                    ShopCheckoutResultPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToClient(ServerPlayer player, boolean success, double newBalance) {
        player.connection.send(new ClientboundCustomPayloadPacket(
                new ShopCheckoutResultPayload(success, newBalance)));
    }
}