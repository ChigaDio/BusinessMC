package com.businessmc.businessmcmod.client.ui.menu;

import com.businessmc.businessmcmod.entity.belmond.BelmondEntity;
import com.businessmc.businessmcmod.network.payload.ButtonResultPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

public class BelmondMenu extends AbstractContainerMenu {
    private final BelmondEntity entity;

    private final List<String> buttonLabels;

    public BelmondMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        super(MenuRegistration.BELMOND_MENU.get(), containerId);
        this.entity = getEntityFromBuf(data, playerInventory.player.level());
        this.buttonLabels = new ArrayList<>();
        int buttonCount = data.readInt();
        for (int i = 0; i < buttonCount; i++) {
            buttonLabels.add(data.readUtf());
        }
        System.out.println("Client: Received " + buttonCount + " button labels: " + buttonLabels);
    }

    // サーバー側で使用するコンストラクタ（直接エンティティを渡す）
    public BelmondMenu(int containerId, Inventory playerInventory, BelmondEntity entity) {
        super(MenuRegistration.BELMOND_MENU.get(), containerId);
        this.entity = entity;
        this.buttonLabels = new ArrayList<>();
    }

    private static BelmondEntity getEntityFromBuf(RegistryFriendlyByteBuf data, Level level) {
        if (data == null) {
            System.err.println("Client: RegistryFriendlyByteBuf is null in getEntityFromBuf");
            throw new IllegalStateException("RegistryFriendlyByteBuf is null");
        }
        int entityId = data.readInt();
        Entity entity = level.getEntity(entityId);
        if (entity instanceof BelmondEntity belmondEntity) {
            System.out.println("Client: Successfully retrieved BelmondEntity ID: " + entityId);
            return belmondEntity;
        }
        System.err.println("Client: Invalid entity ID: " + entityId + " (not a BelmondEntity)");
        throw new IllegalStateException("Invalid entity ID: " + entityId + " (not a BelmondEntity)");
    }

    @Override
    public boolean stillValid(Player player) {
        return entity != null && !entity.isRemoved() && player.distanceToSqr(entity) <= 64.0D;
    }

    public List<String> getButtonLabels() {
        return buttonLabels;
    }

    public void sendButtonClick(int buttonIndex) {
        //ここでリザルト処理

    }

    public BelmondEntity getEntity() {
        return entity;
    }


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        //Slot slot = this.slots.get(index);
        //if (slot != null && slot.hasItem()) {
        //    ItemStack itemstack1 = slot.getItem();
        //    itemstack = itemstack1.copy();
        //    if (index < 9) {
        //        if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
        //            return ItemStack.EMPTY;
        //        }
        //    } else {
        //        if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
        //            return ItemStack.EMPTY;
        //        }
        //    }
        //    if (itemstack1.isEmpty()) {
        //        slot.set(ItemStack.EMPTY);
        //    } else {
        //        slot.setChanged();
        //    }
        //    if (itemstack1.getCount() == itemstack.getCount()) {
        //        return ItemStack.EMPTY;
        //    }
        //    slot.onTake(player, itemstack1);
        //}
        return itemstack;
    }




}