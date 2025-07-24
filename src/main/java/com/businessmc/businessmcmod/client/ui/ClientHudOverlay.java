package com.businessmc.businessmcmod.client.ui;



import com.businessmc.businessmcmod.client.PlayerClientData;
import com.businessmc.businessmcmod.util.collection.function.UtilExecute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientHudOverlay {

    public ClientHudOverlay() {
        // イベントバスにこのクラスを登録
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public  void onRenderGui(RenderGuiEvent.Post event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font; // デフォルトのフォントレンダラーを取得

        int screenWidth = minecraft.getWindow().getWidth();
        int screenHeight = minecraft.getWindow().getHeight();

        String text = "Hello NeoForge HUD!";
        int x = 10; // 左からのX座標
        int y = 10; // 上からのY座標
        int color = 0xFFFFFFFF; // 不透明な白 (ARGB)


        // 例1: 固定座標にシンプルなテキストを表示
        String money_text = "Money:" + PlayerClientData.balance;
        String job_text = "Job:" + UtilExecute.findJobName(PlayerClientData.jobId);
        //guiGraphics.drawString(font, text, x, y, color, true); // trueで影を付ける
        x =20; // 左からのX座標
        y = 30; // 上からのY座標
        color = 0xFFFFFFFF; // 不透明な白 (ARGB)
        guiGraphics.drawString(font, money_text, x, y, color, true); // trueで影を付ける
        guiGraphics.drawString(font, job_text, x, y + 30, color, true); // trueで影を付ける
    }

}