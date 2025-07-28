package com.businessmc.businessmcmod.client.ui.menu;

import com.businessmc.businessmcmod.entity.belmond.BelmondEntity;
import com.businessmc.businessmcmod.entity.businesstradeentity.BusinessTradeEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class BusinessShopMenu extends AbstractContainerMenu {
    private final BusinessTradeEntity entity;



    public BusinessShopMenu(int syncId, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        super(MenuRegistration.BUSINESS_SHOP_MENU.get(), syncId);
        this.entity = getEntityFromBuf(buf, playerInv.player.level());
        entity.RenderInitList();
    }

    public List<Item> getBuyList() {
        return entity.getBuyableItems();
    }
    public List<Item> getSellList() {
        return entity.getSellableItems();
    }
    public BusinessTradeEntity getEntity() {
        return entity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemstack = ItemStack.EMPTY;

        return itemstack;
    }

    private static BusinessTradeEntity getEntityFromBuf(RegistryFriendlyByteBuf data, Level level) {
        if (data == null) {
            System.err.println("Client: RegistryFriendlyByteBuf is null in getEntityFromBuf");
            throw new IllegalStateException("RegistryFriendlyByteBuf is null");
        }
        int entityId = data.readInt();
        Entity entity = level.getEntity(entityId);
        if (entity instanceof BusinessTradeEntity businessTradeEntity) {
            System.out.println("Client: Successfully retrieved BelmondEntity ID: " + entityId);
            return businessTradeEntity;
        }
        System.err.println("Client: Invalid entity ID: " + entityId + " (not a BelmondEntity)");
        throw new IllegalStateException("Invalid entity ID: " + entityId + " (not a BelmondEntity)");
    }

    @Override public boolean stillValid(net.minecraft.world.entity.player.Player player) { return true; }
}