package com.businessmc.businessmcmod.entity;

import com.businessmc.businessmcmod.BusinessMCMod;
import com.businessmc.businessmcmod.entity.belmond.BelmondEntity;
import com.businessmc.businessmcmod.entity.belmond.BelmondEntityRender;
import com.businessmc.businessmcmod.entity.businesstradeentity.BusinessTradeEntity;
import com.businessmc.businessmcmod.entity.businesstradeentity.BusinessTradeEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EntityRegistration {
    // ① DeferredRegister を作成
// DeferredRegister for EntityTypes
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, BusinessMCMod.MOD_ID);

    // ResourceKey for BelmondEntity
    public static final ResourceKey<EntityType<?>> BELMOND_ENTITY_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(BusinessMCMod.MOD_ID, "belmond_entity"));

    public static final ResourceKey<EntityType<?>> BUSINESS_TRADE_ENTITY_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(BusinessMCMod.MOD_ID, "business_trade_entity"));

    // Register EntityType
    public static final DeferredHolder<EntityType<?>, EntityType<BelmondEntity>> BELMOND_ENTITY =
            ENTITY_TYPES.register("belmond_entity",
                    () -> EntityType.Builder.of(BelmondEntity::new, MobCategory.MISC)
                            .sized(0.6F, 1.8F) // サイズ（幅、高さ）
                            .clientTrackingRange(8) // クライアントの追跡範囲
                            .updateInterval(1) // 更新間隔
                            .build(BELMOND_ENTITY_KEY) // ResourceKeyを渡す
            );

    public static final DeferredHolder<EntityType<?>, EntityType<BusinessTradeEntity>> BUSINESS_TRADE_ENTITY =
            ENTITY_TYPES.register("business_trade_entity",
                    () -> EntityType.Builder.of(BusinessTradeEntity::new, MobCategory.MISC)
                            .sized(0.6F, 1.8F) // サイズ（幅、高さ）
                            .clientTrackingRange(2) // クライアントの追跡範囲
                            .updateInterval(60) // 更新間隔

                            .build(BUSINESS_TRADE_ENTITY_KEY) // ResourceKeyを渡す
            );

    public EntityRegistration(IEventBus modEventBus) {
        // Register DeferredRegister to the mod event bus
        ENTITY_TYPES.register(modEventBus);

        //描画も適応

    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistration.BELMOND_ENTITY.get(), BelmondEntityRender::new);

        //ショップ
        event.registerEntityRenderer(EntityRegistration.BUSINESS_TRADE_ENTITY.get(),
                BusinessTradeEntityRenderer::new);
    }
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        // NeoForge 版なら .value()、Forge 版なら .get() で取り出す
        EntityType<BelmondEntity> type = EntityRegistration.BELMOND_ENTITY.value();
        AttributeSupplier attributes = BelmondEntity.createAttributes().build();
        event.put(type, attributes);

        EntityType<BusinessTradeEntity> business_trade_type = EntityRegistration.BUSINESS_TRADE_ENTITY.value();
        AttributeSupplier business_trade_attributes = BusinessTradeEntity.createAttributes().build();
        event.put(business_trade_type, business_trade_attributes);
    }

}