package com.businessmc.businessmcmod.entity.businesstradeentity;

import com.businessmc.businessmcmod.BusinessMCMod;
import com.businessmc.businessmcmod.entity.belmond.BelmondEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;

public class BusinessTradeEntityRenderer extends HumanoidMobRenderer<BusinessTradeEntity, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/villager/villager.png");

    public BusinessTradeEntityRenderer(EntityRendererProvider.Context context) {
        super(context,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER),
                        (texture) -> RenderType.entityCutoutNoCull(texture)),
                0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(HumanoidRenderState state) {
        return TEXTURE;
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }

    @Override
    public void extractRenderState(BusinessTradeEntity entity, HumanoidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }



}