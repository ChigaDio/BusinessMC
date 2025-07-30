package com.businessmc.businessmcmod.client.ui.custom.button;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CustomImageButton extends ImageButton {


    private static final ResourceLocation BASE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("businessmcmod", "textures/gui/choicebtn_black.png");
    private static final ResourceLocation HOVER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("businessmcmod", "textures/gui/choicebtn_pink.png");
    private final Font font;
    private static final int TEXTURE_WIDTH = 660; // テクスチャの実際の幅
    private static final int TEXTURE_HEIGHT = 80; // テクスチャの実際の高さ

    private  int start = 0;
    public CustomImageButton(int x, int y, int width, int height, OnPress onPress, Component message) {
        super(x, y, width, height, null, onPress, message);
        this.font = Minecraft.getInstance().font; // FontをMinecraftから取得

        start = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // デバッグ用ログ
        System.out.println("Rendering CustomImageButton at (" + this.getX() + ", " + this.getY() + "), Hovered: " + this.isHovered());
        // ホバー状態に応じてテクスチャを選択
        ResourceLocation texture =  this.isMouseOver((double)mouseX,(double)mouseY) ? HOVER_TEXTURE : BASE_TEXTURE;

        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                this.getX(),
                this.getY(),
                0,
                0,
                this.width,this.height,
                this.width, this.height,
                ARGB.white(this.alpha)
        );

        guiGraphics.drawString(font,this.getMessage().getString(),this.getX() + 30,this.getY() + ((this.height / 2) - (this.height / 5)),0xFFFFFFFF,true);


    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {

        int i = this.getX() + 5;
        int j = i + this.width - 5;
        int k = this.getY() + 5;
        int l = k + this.height - 5;
        var result = mouseX >= i && mouseX <= j && mouseY >= k && mouseY <= l;
        return  result;

    }

}