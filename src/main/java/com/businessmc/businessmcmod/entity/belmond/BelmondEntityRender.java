package com.businessmc.businessmcmod.entity.belmond;

import com.businessmc.businessmcmod.BusinessMCMod;
import com.businessmc.businessmcmod.entity.businesstradeentity.BusinessTradeEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;

public class BelmondEntityRender extends LivingEntityRenderer<BelmondEntity, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(BusinessMCMod.MOD_ID, "textures/entity/belmond_entity.png");

    public BelmondEntityRender(EntityRendererProvider.Context context) {
        super(context,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER),
                        (texture) -> RenderType.entityCutoutNoCull(texture)),
                0.5f);



    }
    @Override
    protected boolean shouldShowName(BelmondEntity entity, double distanceToCameraSq) {
        if (distanceToCameraSq > 10.0 * 10.0) {
            return false;
        }
        return true;  // クロスヘア判定を完全にバイパス
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
    public void extractRenderState(BelmondEntity entity, HumanoidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

    }


}