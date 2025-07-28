package com.businessmc.businessmcmod.client.ui.screen;

import com.businessmc.businessmcmod.client.ui.menu.BusinessShopMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mongodb.lang.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BusinessShopScreen extends AbstractContainerScreen<BusinessShopMenu> {
    // フィールドとして
    private ItemEntity displayItemEntity;
    // AMOUNT_INPUT に切り替わったタイミングで初期化
    private void ensureDisplayEntity() {
        if (displayItemEntity == null && selectedItem != null) {
            // ClientLevel が必要なので、minecraft.level を渡す
            displayItemEntity = new ItemEntity(
                    minecraft.level,
                    0, 0, 0,
                    new ItemStack(selectedItem)
            );
            // プレイヤーが拾わないように
            displayItemEntity.setPickUpDelay(Integer.MAX_VALUE);
        }
    }

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/book.png");

    private enum State { CATEGORY, ITEM_LIST, AMOUNT_INPUT }
    private State state = State.CATEGORY;

    private static final int COLUMNS = 3;
    private static final int ROWS = 5;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_PADDING = 4;

    private static final int VISIBLE_ROWS = 8;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_SPACING = 4;


    private int scrollOffset = 0;
    private int maxOffset = 0;
    private List<Item> currentList;
    private Item selectedItem;
    private EditBox amountInput;

    //スクロール
    private static final int SCROLLBAR_WIDTH = 8;
    private boolean draggingScroll = false;
    private int dragOffsetY;  // サムを掴んだときのマウスY - サムY
    private int sbX, sbY, sbHeight, thumbHeight, thumbY;
    // 新：アイテムボタンを保持するリスト
    private final List<AbstractButton> itemButtons = new ArrayList<>();

    public BusinessShopScreen(BusinessShopMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

    }

    @Override
    protected void init() {
        super.init();
        // 毎回クリア
        this.renderables.clear();
        this.children().clear();
        itemButtons.clear();

        // 常に戻るボタンは用意。挙動は state によって変える
        addRenderableWidget(Button.builder(Component.literal("< Back"), btn -> {
            switch (state) {
                case CATEGORY     -> this.minecraft.setScreen(null);
                case ITEM_LIST    -> { state = State.CATEGORY; init(); }
                case AMOUNT_INPUT -> { state = State.ITEM_LIST; init(); }
            }
        }).bounds(leftPos + 4, topPos + 4, 60, 18).build());

        switch (state) {
            case CATEGORY     -> initCategory();
            case ITEM_LIST    -> initItemList();
            case AMOUNT_INPUT -> initAmountInput();
        }
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        // ITEM_LIST 状態かつスクロール可能なら
        if (state == State.ITEM_LIST && maxOffset > 0) {
            // Y方向のホイールだけを使う (+1/-1)
            int dir = (int) Math.signum(scrollDeltaY);
            // 上スクロールで dir=+1 → scrollOffset--、下スクロールで dir=-1 → scrollOffset++
            scrollOffset = Mth.clamp(scrollOffset - dir, 0, maxOffset);
            init();   // ボタン群を再生成
            return true;
        }
        // それ以外は親クラス処理に任せる（もし必要なら）
        return super.mouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
    }


    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // ホイールで scrollOffset を変更して再配置
        scrollOffset = Mth.clamp(scrollOffset - (int) delta, 0, maxOffset);
        init(); // ボタン群を作り直す
        return true;
    }

    private void addBackButton() {
        addRenderableWidget(Button.builder(Component.literal("<"), btn -> {
            if (state == State.ITEM_LIST) state = State.CATEGORY;
            else if (state == State.AMOUNT_INPUT) state = State.ITEM_LIST;
            this.minecraft.setScreen(null); // メニューを閉じる
        }).bounds(leftPos + 4, topPos + 4, 20, 20).build());
    }

    private void initCategory() {
        addRenderableWidget(Button.builder(Component.literal("Buy"), btn -> {
            state = State.ITEM_LIST;
            currentList = menu.getBuyList();
            computeMaxOffset();
            init();
        }).bounds(leftPos + 30, topPos + 40, 50, 18).build());

        addRenderableWidget(Button.builder(Component.literal("Sell"), btn -> {
            state = State.ITEM_LIST;
            currentList = menu.getSellList();
            computeMaxOffset();
            init();
        }).bounds(leftPos + 100, topPos + 40, 50, 18).build());
    }

    private void computeMaxOffset() {
        int totalRows = Mth.ceil((float) currentList.size() / 1); // 1列なので /1
        maxOffset = Math.max(0, totalRows - VISIBLE_ROWS);
        scrollOffset = 0;
    }



    private void initItemList() {
        // 1列に並べる
        for (int i = 0; i < currentList.size(); i++) {
            int row = i - scrollOffset;
            if (row < 0 || row >= VISIBLE_ROWS) continue;

            int x = leftPos + 10;
            int y = topPos + 30 + row * (BUTTON_HEIGHT + BUTTON_SPACING);
            Item item = currentList.get(i);

            Button b = Button.builder(Component.literal(item.getDescriptionId()), btn -> {
                        selectedItem = item;
                        state = State.AMOUNT_INPUT;
                        init();
                    })
                    .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build();
            addRenderableWidget(b);
            itemButtons.add(b);
        }
    }

    private void initAmountInput() {
        amountInput = new EditBox(font, leftPos + 60, topPos + 60, 40, 18, Component.literal("Qty"));
        amountInput.setValue("1");
        addRenderableWidget(amountInput);
        addRenderableWidget(Button.builder(Component.literal("OK"), btn -> {
            int qty;
            try { qty = Integer.parseInt(amountInput.getValue()); } catch (NumberFormatException e) { return; }
            // サーバーへリクエスト
            //TradePayload.sendToServer(selectedItem, qty, menu.getEntity().getId());
            minecraft.setScreen(null);
        }).bounds(leftPos + 110, topPos + 60, 30, 18).build());
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        // まずスクロール処理（mouseScrolled が使えないのでここで）
        int scroll = (int) this.minecraft.mouseHandler.getYVelocity();
        if (scroll != 0) {
            scrollOffset = Mth.clamp(scrollOffset - scroll, 0, maxOffset);
            init();  // 再構築
        }

        renderBackground(gui,mouseX,mouseY,partialTick);
        super.render(gui, mouseX, mouseY, partialTick);
        //gui.renderTooltip(font, List.of(), mouseX, mouseY); // ツールチップは個別に出す

        // アイテムリスト中はアイコンも重ねる
        if (state == State.ITEM_LIST) {
            for (int i = 0; i < itemButtons.size(); i++) {
                var b = itemButtons.get(i);
                ItemStack stack = new ItemStack(currentList.get(i + scrollOffset));
                gui.renderItem(stack, b.getX() + 2, b.getY() + 2);
            }
        }



    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //RenderSystem.setShaderTexture(0, BACKGROUND);
        // 背景にインベントリの灰色チェック柄を描画
        gui.pose().pushMatrix();
        gui.pose().translate(-384, 0);
        gui.pose().scale(2.5f, 1f);
        gui.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                256, 256
        );
        gui.pose().popMatrix();

        if(state == State.ITEM_LIST) {
            sbX      = leftPos + imageWidth - 14;
            sbY      = topPos + 30;
            sbHeight = VISIBLE_ROWS * (BUTTON_HEIGHT + BUTTON_SPACING);
            gui.fill(sbX, sbY, sbX + SCROLLBAR_WIDTH, sbY + sbHeight, 0xFF444444);

            // サム（つまみ）
            int totalItems = currentList.size();
            thumbHeight = Math.max(10, sbHeight * VISIBLE_ROWS / Math.max(totalItems, VISIBLE_ROWS));
            thumbY      = sbY + (totalItems > VISIBLE_ROWS
                    ? scrollOffset * (sbHeight - thumbHeight) / (totalItems - VISIBLE_ROWS)
                    : 0);
            gui.fill(sbX + 1, thumbY, sbX + SCROLLBAR_WIDTH - 1, thumbY + thumbHeight, 0xFFAAAAAA);
        }

        if (state == State.AMOUNT_INPUT && selectedItem != null) {
            // 大きく表示したい座標
            int baseX = leftPos + 20;
            int baseY = topPos  + 40;
            ItemStack select_stack_render = new ItemStack(selectedItem);

            // 1) PoseStack でスケール
            Matrix3x2fStack ps = gui.pose();
            ps.pushMatrix();
            // 原点を (baseX, baseY) に移動してから拡大
            ps.translate(baseX, baseY, ps);
            ps.scale(3.0f, 3.0f, ps);
            ps.translate(-baseX, -baseY, ps);


            // 2) アイテム描画
            gui.renderItem(select_stack_render, baseX, baseY);
            gui.renderItemDecorations(font, select_stack_render, baseX, baseY, "");

            // 3) PoseStack を戻す
            ps.popMatrix();

            // 4) アイテム名を取得して描画
            //String name = select_stack_render.getHoverName().getString();
            //gui.drawString(font, name, baseX + 40, baseY, 0xFFFFFF, true);
        }
    }

    private void drawItems(GuiGraphics gui, int mouseX, int mouseY) {
        int start = scrollOffset * COLUMNS;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int idx = start + row * COLUMNS + col;
                if (idx >= currentList.size()) break;
                int px = leftPos + 10 + col * (SLOT_SIZE + SLOT_PADDING);
                int py = topPos + 20 + row * (SLOT_SIZE + SLOT_PADDING);
                ItemStack stack = new ItemStack(currentList.get(idx));
                gui.renderItem(stack, px, py); // 装飾も含めて描画
                if (mouseX >= px && mouseX < px + SLOT_SIZE && mouseY >= py && mouseY < py + SLOT_SIZE) {
                    renderTooltip(gui, mouseX, mouseY);
                }
            }
        }
        // スクロールバーの描画は変更不要
        int sbx = leftPos + imageWidth - 12;
        int sby = topPos + 20;
        int sbh = ROWS * (SLOT_SIZE + SLOT_PADDING);
        gui.fill(sbx, sby, sbx + 6, sby + sbh, 0xFF555555);
        int totalRows = Mth.ceil((float) currentList.size() / COLUMNS);
        int thumbH = sbh * ROWS / totalRows;
        int thumbY = sby + (totalRows > ROWS ? scrollOffset * (sbh - thumbH) / (totalRows - ROWS) : 0);
        gui.fill(sbx, thumbY, sbx + 6, thumbY + thumbH, 0xFFAAAAAA);
    }
    @Override
    public void clearWidgets() {
        // スロットは描画しないのでメニュースロットも消したい場合は更に slotWidgets.clear() など
        this.renderables.clear();
        this.children().clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {


        // サムをクリックしたか？
        if (state == State.ITEM_LIST
                && mouseX >= sbX + 1 && mouseX < sbX + SCROLLBAR_WIDTH - 1
                && mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
            draggingScroll = true;
            dragOffsetY    = (int)(mouseY - thumbY);
            return true;
        }

        // まず親処理（ボタンなどへのクリック）
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScroll) {
            // マウスのY位置からトラック内の相対位置を出す
            int mouseRelY = (int)mouseY - sbY - dragOffsetY;
            int maxTrack  = sbHeight - thumbHeight;
            mouseRelY     = Mth.clamp(mouseRelY, 0, maxTrack);

            // トラック比から scrollOffset に変換
            int totalItems = currentList.size();
            int maxOff     = Math.max(0, totalItems - VISIBLE_ROWS);
            scrollOffset   = Math.round(mouseRelY * (float)maxOff / maxTrack);
            init();  // ウィジェット再生成
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingScroll) {
            draggingScroll = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {

        // フォントシャドウ＋カラーで “おしゃれ” に
        String money = "¥10000";
        int x = imageWidth - font.width(money) - 10;
        int y = 6;
        gui.drawString(font, money, x, y,
                0xFFD700, // ゴールドカラー
                true      // シャドウ
        );
    }


}
