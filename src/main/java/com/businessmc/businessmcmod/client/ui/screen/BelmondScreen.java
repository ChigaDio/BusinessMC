package com.businessmc.businessmcmod.client.ui.screen;

    import com.businessmc.businessmcmod.client.ui.menu.BelmondMenu;
    import com.businessmc.businessmcmod.entity.belmond.BelmondEntity;
    import com.businessmc.businessmcmod.network.payload.ButtonClickPayload;
    import com.mojang.blaze3d.systems.RenderSystem;
    import net.minecraft.client.gui.GuiGraphics;
    import net.minecraft.client.gui.components.Button;
    import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
    import net.minecraft.client.renderer.GameRenderer;
    import net.minecraft.network.chat.Component;
    import net.minecraft.resources.ResourceLocation;
    import net.minecraft.world.entity.player.Inventory;
    
    import java.util.List;
    
    public class BelmondScreen extends AbstractContainerScreen<BelmondMenu> {
        private static final ResourceLocation TEXTURE =
                ResourceLocation.fromNamespaceAndPath("businessmcmod", "textures/gui/belmond_menu.png");
    
        private int scrollOffset;
        private static final int BUTTONS_PER_ROW = 2;
        private static final int BUTTON_WIDTH = 80;
        private static final int BUTTON_HEIGHT = 20;
        private static final int BUTTON_SPACING = 5;
        private int totalRows;
        public BelmondScreen(BelmondMenu menu, Inventory playerInventory, Component title) {
            super(menu, playerInventory, title);
            this.imageWidth = 300;
            this.imageHeight = 150;
            this.inventoryLabelY = this.imageHeight;
        }
    
        @Override
        protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    
            // 背景にインベントリの灰色チェック柄を描画
            int x = (this.width / 2) - (int)((this.imageWidth / 1.5) / 2); // スクリーン幅の中央
    
    
            guiGraphics.blit(TEXTURE, x, 0,x + (int)(this.imageWidth / 1.5),(int)(this.height / 2.5),0,1,0,1);
        }
    
        @Override
        protected void init() {
            super.init();
            List<String> buttonLabels = this.menu.getButtonLabels();
            totalRows = (int) Math.ceil((double) buttonLabels.size() / BUTTONS_PER_ROW);
            int visibleRows = 5; // 表示可能な行数
    
            for (int i = 0; i < buttonLabels.size(); i++) {
                int row = i / BUTTONS_PER_ROW;
                int col = i % BUTTONS_PER_ROW;
                if (row < visibleRows) {
                    int x = this.leftPos + 10 + col * (BUTTON_WIDTH + BUTTON_SPACING);
                    int y = this.topPos + 20 + row * (BUTTON_HEIGHT + BUTTON_SPACING);
                    int finalI = i;
                    addRenderableWidget(Button.builder(Component.literal(buttonLabels.get(i)),
                                    btn -> onButtonClick(finalI))
                            .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                            .build());
                }
            }
        }
    
        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
            // 背景とボタン描画
            renderBg(guiGraphics, partialTick, mouseX, mouseY);
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            //this.renderTooltip(guiGraphics, mouseX, mouseY);
    
    
    
            // スクロール処理
            handleScroll(mouseX, mouseY);
            renderButtons(guiGraphics, mouseX, mouseY);
        }
    
    
    
        @Override
        protected void renderMenuBackground(GuiGraphics guiGraphics, int x, int y, int width, int height) {
            super.renderMenuBackground(guiGraphics, x, y, width, height);
        }
    
        private void handleScroll(int mouseX, int mouseY) {
            int scrollWheel = getScrollWheel();
            if (scrollWheel != 0) {
                scrollOffset = Math.max(0, Math.min(scrollOffset - scrollWheel, totalRows - 5));
            }
        }
    
        private void renderButtons(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            List<String> buttonLabels = this.menu.getButtonLabels();
            int visibleRows = 5;
    
            for (int i = scrollOffset * BUTTONS_PER_ROW; i < Math.min((scrollOffset + visibleRows) * BUTTONS_PER_ROW, buttonLabels.size()); i++) {
                int row = (i / BUTTONS_PER_ROW) - scrollOffset;
                int col = i % BUTTONS_PER_ROW;
                int x = this.leftPos + 10 + col * (BUTTON_WIDTH + BUTTON_SPACING);
                int y = this.topPos + 20 + row * (BUTTON_HEIGHT + BUTTON_SPACING);
                guiGraphics.drawString(this.font, buttonLabels.get(i), x, y, 0xFFFFFF);
                // ボタン描画は init で処理済み、こちらではテキストのみ更新
            }
    
            // スクロールバーの簡易表示（仮実装）
            int scrollBarX = this.leftPos + this.imageWidth - 15;
            int scrollBarY = this.topPos + 20;
            int scrollBarHeight = 120;
            int scrollThumbHeight = (int) (scrollBarHeight * (visibleRows / (float) totalRows));
            int scrollThumbY = scrollBarY + (int) ((scrollOffset / (float) (totalRows - visibleRows)) * (scrollBarHeight - scrollThumbHeight));
            guiGraphics.fill(scrollBarX, scrollBarY, scrollBarX + 10, scrollBarY + scrollBarHeight, 0xFF_555555);
            guiGraphics.fill(scrollBarX, scrollThumbY, scrollBarX + 10, scrollThumbY + scrollThumbHeight, 0xFF_AAAAAA);
        }
    
        private int getScrollWheel() {
            if (this.minecraft != null && this.minecraft.mouseHandler != null) {
                return (int)this.minecraft.mouseHandler.getYVelocity();
            }
            return 0;
        }
    
        private void onButtonClick(int buttonIndex) {
            this.menu.sendButtonClick(buttonIndex);
            BelmondEntity entity = this.menu.getEntity();
            if (entity != null) {
    
                ButtonClickPayload.sendToServer(buttonIndex, entity.getId());
                this.minecraft.setScreen(null); // メニューを閉じる
            }
        }

}