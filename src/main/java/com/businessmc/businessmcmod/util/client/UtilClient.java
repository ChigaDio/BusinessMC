package com.businessmc.businessmcmod.util.client;

import com.businessmc.businessmcmod.util.collection.ItemBlockGameCollectionData;
import com.businessmc.businessmcmod.util.collection.ItemBlockGameCollectionDb;
import com.businessmc.businessmcmod.util.collection.JobTypeCollectionDb;
import com.businessmc.businessmcmod.util.collection.UserGamePlayerCollectionData;
import com.businessmc.businessmcmod.util.general.UtilGeneral;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.*;

//クライアント側のUtil
public class UtilClient {
    ///プレイヤーがこのサーバーに初めてログインしたとき
    public  static  void ClientNewInit(PlayerEvent.PlayerLoggedInEvent event, MongoDatabase db)
    {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        //存在チェック
        var check_result = ItemBlockGameCollectionDb.findAllItemBlockMany(db);
        if(!check_result.isSuccess()) return;
        if(check_result.getResult().size() <= 0) return;

        List<ItemBlockGameCollectionData> add_list = new ArrayList<>();

        // アイテム一覧

        Set<String> addedIdNames = new HashSet<>();

// アイテム一覧
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            String idName = id.getPath();

            // すでに追加済みならスキップ
            if (addedIdNames.contains(idName)) continue;


            ItemStack stack = new ItemStack(item);
            String name = stack.getHoverName().getString();

            ItemBlockGameCollectionData add_data = new ItemBlockGameCollectionData();
            add_data.setRate(1.0);
            add_data.setIdName(idName);
            add_data.setNameLang(name);
            add_data.setPrice(10.0);
            add_data.setActionPriceJob(new ArrayList<>());
            add_data.setRecipeCreateJobList(new ArrayList<>());
            // タグ情報を追加
            add_data.setTagsList(UtilGeneral.getItemTagNames(item));

            add_list.add(add_data);
            addedIdNames.add(idName);
        }

        // ブロック一覧
        for (Block block : BuiltInRegistries.BLOCK) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            String idName = id.getPath();

            // すでに追加済みならスキップ
            if (addedIdNames.contains(idName)) continue;



            ItemStack stack = new ItemStack(block.asItem());
            String name = stack.getHoverName().getString();

            ItemBlockGameCollectionData add_data = new ItemBlockGameCollectionData();
            add_data.setRate(1.0);
            add_data.setIdName(idName);
            add_data.setNameLang(name);
            add_data.setPrice(10.0);
            add_data.setActionPriceJob(new ArrayList<>());
            add_data.setRecipeCreateJobList(new ArrayList<>());
            // タグ情報を追加
            add_data.setTagsList(UtilGeneral.getBlockTagNames(block));

            add_list.add(add_data);
            addedIdNames.add(idName);
        }

        // エンティティ一覧

        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            String name = entityType.getDescription().getString(); // エンティティ名

        }

        //MongoDBに保存
        ItemBlockGameCollectionDb.bulkInsertItemBlockGameCollectionData(db,add_list);


    }





    public  static double BreakBlockPrice(Block blok, MongoDatabase db, ClientSession client, UserGamePlayerCollectionData data)
    {
        if(data == null) return 0.0;

        var result = UtilGeneral.getBlockPath(blok);

        //壊したブロックが壊したプレイヤーの職業で報酬が得られるか
        var result_job_recipe = ItemBlockGameCollectionDb.findActionJobOne(db,client,data.getJobId());
        if(!result_job_recipe.isSuccess()) return 0.0;


        var find_data = ItemBlockGameCollectionDb.findIdNameOne(db,client,result);
        if(!find_data.isSuccess()) return 0.0;

        var find_job_data = JobTypeCollectionDb.findJobIdOne(db,client,data.getJobId());
        if(!find_job_data.isSuccess()) return 0.0;

        var reward = (find_data.getResult().getPrice() * find_job_data.getResult().getSellRatio());

        return reward;
    }



}
