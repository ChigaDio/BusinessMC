package com.businessmc.businessmcmod.client.ui.menu.provider;

import com.businessmc.businessmcmod.client.ui.menu.BusinessShopMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import com.businessmc.businessmcmod.entity.businesstradeentity.BusinessTradeEntity;
import net.minecraft.server.MinecraftServer;
public class BusinessTradeMenuProvider implements MenuProvider {
    private final BusinessTradeEntity entity;

    public BusinessTradeMenuProvider(BusinessTradeEntity entity) {
        this.entity = entity;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
        RegistryFriendlyByteBuf buf =new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
        buf.writeInt(entity.getId());
        return new BusinessShopMenu(syncId, playerInv, buf);
    }
}