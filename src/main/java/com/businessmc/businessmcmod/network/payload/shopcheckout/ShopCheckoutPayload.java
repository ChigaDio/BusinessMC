package com.businessmc.businessmcmod.network.payload.shopcheckout;

import com.businessmc.businessmcmod.BusinessMCMod;
import com.businessmc.businessmcmod.network.payload.ButtonClickPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public record ShopCheckoutPayload(String PlayerId ,int ShopID, Boolean BuyFlag,int ItemCount,String ItemName,double Amout) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ShopCheckoutPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BusinessMCMod.MOD_ID,
                    "checkout"));

    public static final StreamCodec<FriendlyByteBuf, ShopCheckoutPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,               // ShopID
                    ShopCheckoutPayload::PlayerId,
                    ByteBufCodecs.VAR_INT,               // ShopID
                    ShopCheckoutPayload::ShopID,
                    ByteBufCodecs.BOOL,               // ShopID
                    ShopCheckoutPayload::BuyFlag,
                    ByteBufCodecs.VAR_INT,               // ShopID
                    ShopCheckoutPayload::ItemCount,
                    ByteBufCodecs.STRING_UTF8,               // ItemName
                    ShopCheckoutPayload::ItemName,
                    ByteBufCodecs.DOUBLE,               // ItemName
                    ShopCheckoutPayload::Amout,
                    ShopCheckoutPayload::new              // デコード時コンストラクタ参照
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToServer(String playerId, int shopID,Boolean buyFlag,int itemCount,String itemName,double amout) {
        // NeoForge 標準の PacketDistributor を使う
        ClientPacketDistributor.sendToServer( new ShopCheckoutPayload(playerId,shopID, buyFlag,itemCount,itemName,amout));
    }


    public static void sendToClient(ServerPlayer player,String playerId,int shopID,Boolean buyFlag, int itemCount,String itemName, double amout) {
        // NeoForge 標準の PacketDistributor を使う
        player.connection.send(new ClientboundCustomPayloadPacket(
                ( new ShopCheckoutPayload(playerId,shopID,buyFlag, itemCount,itemName,amout))));
    }
}
