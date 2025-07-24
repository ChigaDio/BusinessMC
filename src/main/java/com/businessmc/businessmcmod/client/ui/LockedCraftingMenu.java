package com.businessmc.businessmcmod.client.ui;

import com.businessmc.businessmcmod.mongodb.MongoDBConnectionManager;
import com.businessmc.businessmcmod.util.collection.ItemBlockGameCollectionDb;
import com.businessmc.businessmcmod.util.collection.UserGamePlayerCollectionDb;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class LockedCraftingMenu extends CraftingMenu {
    private final ServerPlayer player;

    public LockedCraftingMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, ServerPlayer player) {
        super(containerId, playerInventory, access);
        this.player = player;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        if (!(container instanceof CraftingContainer crafting)) return;

        // 1. スロットから ItemStack をリスト化
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < crafting.getContainerSize(); i++) {
            items.add(crafting.getItem(i));
        }

        // 2. クラフトマトリクスのサイズを判定（例：2x2 or 3x3）
        int width = crafting.getWidth();
        int height = crafting.getHeight();

        // 3. CraftingInput を作成
        CraftingInput input = CraftingInput.of(width, height, items);

        // 4. レシピ取得
        RecipeManager manager = player.getServer().getRecipeManager();
        Optional<RecipeHolder<CraftingRecipe>> match =
                manager.getRecipeFor(RecipeType.CRAFTING, input, player.level());

        if (match.isPresent()) {
            var id = match.get().id();
            var gamedatabase = MongoDBConnectionManager.getInstance().getDatabase("game");
            //プレイヤーデータを取得
            var player_data = UserGamePlayerCollectionDb.findPlayerGameDataOne(gamedatabase,player.getStringUUID());
            //ItemBlockDataDbから、現在のジョブでチェック
            var result_item_block = ItemBlockGameCollectionDb.findActionBlockJobOne(gamedatabase,player_data.getResult().getJobId(),id.location().getPath());

            if(!result_item_block.isSuccess())
            {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
            }

        }
    }





}
