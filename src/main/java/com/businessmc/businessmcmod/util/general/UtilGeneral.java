package com.businessmc.businessmcmod.util.general;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.codehaus.plexus.util.CachedMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//汎用
public class UtilGeneral {
    public static String getBlockPath(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    // ブロックのタグ名を取得
// ブロックのタグ名を取得
    public static List<String> getBlockTagNames(Block block) {
        List<String> tagNames = new ArrayList<>();
        // すべてのブロックタグを取得し、指定されたブロックが含まれるタグをフィルタリング
        BuiltInRegistries.BLOCK.getTags().forEach(tagEntry -> {
            TagKey<Block> tag = tagEntry.key();
            if (block.defaultBlockState().is(tag)) {
                tagNames.add(tag.location().toString());
            }
        });
        return tagNames;
    }

    // アイテムのタグ名を取得
    public static List<String> getItemTagNames(Item item) {
        List<String> tagNames = new ArrayList<>();
        // すべてのアイテムタグを取得し、指定されたアイテムが含まれるタグをフィルタリング
        BuiltInRegistries.ITEM.getTags().forEach(tagEntry -> {
            TagKey<Item> tag = tagEntry.key();
            if (item.getDefaultInstance().is(tag)) {
                tagNames.add(tag.location().toString());
            }
        });
        return tagNames;
    }

    public static Item getItemFromName(String itemId) {
        var result =  BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", itemId));
        if(result.isEmpty()) return null;
        return result.get().value();
    }

}
