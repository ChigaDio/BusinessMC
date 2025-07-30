package com.businessmc.businessmcmod;

import com.businessmc.businessmcmod.client.PlayerClientData;
import com.businessmc.businessmcmod.client.ui.ClientHudOverlay;
import com.businessmc.businessmcmod.client.ui.LockedCraftingMenu;
import com.businessmc.businessmcmod.client.ui.menu.BelmondMenu;
import com.businessmc.businessmcmod.client.ui.menu.BusinessShopMenu;
import com.businessmc.businessmcmod.client.ui.menu.MenuRegistration;
import com.businessmc.businessmcmod.client.ui.screen.BelmondScreen;
import com.businessmc.businessmcmod.client.ui.screen.BusinessShopScreen;
import com.businessmc.businessmcmod.entity.EntityRegistration;
import com.businessmc.businessmcmod.mongodb.MongoDBConnectionManager;
import com.businessmc.businessmcmod.network.handler.NetworkPayloadRegistration;
import com.businessmc.businessmcmod.network.payload.PlayerDataSyncPayload;
import com.businessmc.businessmcmod.util.collection.*;
import com.businessmc.businessmcmod.util.client.UtilClient;
import com.businessmc.businessmcmod.util.collection.transaction.TransactionExecutor;
import com.businessmc.businessmcmod.util.collection.transaction.TransactionResult;
import com.businessmc.businessmcmod.util.config.BusinessMCCommonConfig;
import com.businessmc.businessmcmod.util.general.UtilGeneral;
import com.mojang.brigadier.CommandDispatcher;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import org.bson.Document;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.screens.MenuScreens;

@Mod(BusinessMCMod.MOD_ID)
public class BusinessMCMod {
    public static final String MOD_ID = "businessmcmod";

    private static MongoClient mongoClient;
    private static MongoDatabase gameDatabase;

    public BusinessMCMod(IEventBus modEventBus, ModContainer modContainer) {

        // コンフィグを登録
        BusinessMCCommonConfig.register();


        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::clientSetup);
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(this::onEntityDeath);
        NeoForge.EVENT_BUS.addListener(this::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(this::onItemFished);

        // Crafting-related event listeners
        NeoForge.EVENT_BUS.addListener(this::onItemCrafted);
        NeoForge.EVENT_BUS.addListener(this::onPlayerInteract);
        NeoForge.EVENT_BUS.addListener(this::onRenderScreen);
        NeoForge.EVENT_BUS.addListener(this::onRightClickBlock);

        new EntityRegistration(modEventBus); // エンティティ登録
        modEventBus.addListener(EntityRegistration::registerRenderers);
        modEventBus.addListener(EntityRegistration::onEntityAttributeCreation);

        new MenuRegistration(modEventBus); // メニュー登録
        modEventBus.addListener(NetworkPayloadRegistration::registerServer);
    }

    public static void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        // クラフトが行われた場合、結果を無効化（インベントリに戻す）
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.level().isClientSide()) {
            // クラフトされたアイテムをインベントリに戻す（クラフトを無効化）
            for (int i = 0; i < event.getCrafting().getCount(); i++) {
                player.getInventory().add(event.getCrafting().copy());
            }
            event.getCrafting().setCount(0); // クラフト結果を空に
            player.sendSystemMessage(Component.literal("クラフトは無効です！"));
        }
    }
    private void onCommonSetup(FMLCommonSetupEvent event) {
        System.out.println("BusinessMCMod: onCommonSetup");

        event.enqueueWork(() ->
        {
            // MongoDB接続を初期化
            mongoClient = MongoDBConnectionManager.getInstance();
            System.out.println("BusinessMCMod: Constructing mod");

            //テスト
            JobTypeCollectionDb.MemoryCacheJobTypeCollectionData(mongoClient.getDatabase("game"));
        });

    }

    private void clientSetup(final FMLClientSetupEvent event) {

        System.out.println("BusinessMCMod: clientSetup");
        event.enqueueWork(() -> {
            try {
                // リフレクションで MenuScreens.register を呼び出し
                Method registerMethod = MenuScreens.class.getDeclaredMethod(
                        "register",
                        MenuType.class,
                        MenuScreens.ScreenConstructor.class
                );
                registerMethod.setAccessible(true);
                // 型を明示的にキャスト
                @SuppressWarnings("unchecked")
                MenuScreens.ScreenConstructor<BelmondMenu, BelmondScreen> constructor = (container, inventory, title) -> new BelmondScreen(container, inventory, title);
                registerMethod.invoke(null, MenuRegistration.BELMOND_MENU.get(), constructor);
                System.out.println("Successfully registered BelmondScreen via reflection");

                MenuScreens.ScreenConstructor<BusinessShopMenu, BusinessShopScreen> busines_shop_constructor = (container, inventory, title) -> new BusinessShopScreen(container, inventory, title);
                registerMethod.invoke(null, MenuRegistration.BUSINESS_SHOP_MENU.get(), busines_shop_constructor);

                new ClientHudOverlay(); // Client-side HUD initialization
            } catch (Exception e) {
                System.err.println("Failed to register BelmondScreen: " + e.getMessage());
                e.printStackTrace();
            }
        });


    }

    public  void onServerStarted(ServerStartedEvent event) {

    }

    private void onServerStarting(ServerStartingEvent event) {

        System.out.println("BusinessMCMod: Server starting");

        gameDatabase = mongoClient.getDatabase("game");

        registerCommands(event);
    }

    private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;



        String uuid = player.getUUID().toString();
        String name = player.getName().getString();

        if (mongoClient == null || gameDatabase == null) {
            System.err.println("MongoDB not initialized");
            return;
        }

        var transaction = new TransactionExecutor(mongoClient);
        transaction.runTransaction("game", (db, session) -> {
            boolean collectionExists = gameDatabase.listCollectionNames()
                    .into(new ArrayList<>())
                    .contains("user_game_player");
            if (!collectionExists) {
                UserGamePlayerCollectionDb.createIndexes(gameDatabase);
            }
            collectionExists = gameDatabase.listCollectionNames()
                    .into(new ArrayList<>())
                    .contains(JobTypeCollectionDb.collection_name);
            if (!collectionExists) {
                JobTypeCollectionDb.createIndexes(gameDatabase);
                List<JobTypeCollectionData> add_data_list = new ArrayList<>() {{
                    add(new JobTypeCollectionData(new Document()
                            .append("job_id", 1)
                            .append("sell_ratio", 0.5)
                            .append("job_name", "放浪者")));
                    add(new JobTypeCollectionData(new Document()
                            .append("job_id", 2)
                            .append("sell_ratio", 0.55)
                            .append("job_name", "平民")));
                    add(new JobTypeCollectionData(new Document()
                            .append("job_id", 3)
                            .append("sell_ratio", 0.75)
                            .append("job_name", "伐採者")));
                    add(new JobTypeCollectionData(new Document()
                            .append("job_id", 4)
                            .append("sell_ratio", 0.75)
                            .append("job_name", "採掘者")));
                    add(new JobTypeCollectionData(new Document()
                            .append("job_id", 5)
                            .append("sell_ratio", 0.75)
                            .append("job_name", "釣り人")));
                    add(new JobTypeCollectionData(new Document()
                            .append("job_id", 6)
                            .append("sell_ratio", 0.65)
                            .append("job_name", "傭兵")));
                    add(new JobTypeCollectionData(new Document()
                            .append("job_id", 7)
                            .append("sell_ratio", 0.7)
                            .append("job_name", "混合農業")));
                }};
                UtilClient.ClientNewInit(event,gameDatabase);
                JobTypeCollectionDb.bulkInsertJobTypeCollectionData(gameDatabase, add_data_list);
                JobTypeCollectionDb.MemoryCacheJobTypeCollectionData(gameDatabase);
            } else {
                JobTypeCollectionDb.MemoryCacheJobTypeCollectionData(gameDatabase);
            }
            ItemBlockGameCollectionDb.MemoryCacheItemBlockGameCollectionData(gameDatabase);
            var playerData = UserGamePlayerCollectionDb.findPlayerGameDataOne(gameDatabase, uuid);
            if (playerData.getResult() == null) {
                UserGamePlayerCollectionData data = new UserGamePlayerCollectionData();
                data.setPlayerId(uuid);
                data.setPlayerName(name);
                data.setJobId(1);
                data.setBalance(1000.0);
                var result_flag = UserGamePlayerCollectionDb.bulkInsertUserGamePlayerCollectionData(gameDatabase,
                        new ArrayList<>() {{
                            add(data);
                        }});
                PlayerClientData.balance = data.getBalance();
                PlayerClientData.jobId = data.getJobId();
                return TransactionResult.flagReturn(result_flag, "User inserted.");
            }
            PlayerClientData.balance = playerData.getResult().getBalance();
            PlayerClientData.jobId = playerData.getResult().getJobId();

            //プレイヤー全体をメモリキャッシュ
            UserGamePlayerCollectionDb.MemoryCacheUserGamePlayerCollectionData(gameDatabase);
            return TransactionResult.failure("User already exists");
        });
    }

    private void onEntityDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        String uuid = player.getUUID().toString();
        var transaction = new TransactionExecutor(mongoClient);
        transaction.runTransactionAsync("game", (db, session) -> {
            var playerData = UserGamePlayerCollectionDb.findPlayerGameDataOne(gameDatabase, uuid);
            if (playerData.getResult() == null) return TransactionResult.failure("Player not found");

            int jobId = playerData.getResult().getJobId();
            double reward = 50.0;
            double sellRatio = getSellRatio(jobId);
            if (jobId == 6) {
                reward *= 1.5;
            }
            double finalReward = reward * sellRatio;

            UserGamePlayerCollectionDb.addBalance(gameDatabase, playerData.getResult().getPlayerName(), finalReward,true);
            player.sendSystemMessage(Component.literal("敵を倒して " + finalReward + " の報酬を得ました！"));
            return TransactionResult.success("Reward added for killing entity");
        });
    }

    private void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        String uuid = player.getUUID().toString();
        Block block = event.getState().getBlock();


        var transaction = new TransactionExecutor(mongoClient);
        transaction.runTransactionAsync("game", (db, session) -> {
            var playerData = UserGamePlayerCollectionDb.findPlayerGameDataOne(gameDatabase, uuid);
            if (playerData.getResult() == null) return TransactionResult.failure("Player not found");

            var add_balance = UtilClient.BreakBlockPrice(block,gameDatabase,session,playerData.getResult());
            if(add_balance == 0.0) return TransactionResult.success("No RewardBalance");

            UserGamePlayerCollectionDb.addBalance(gameDatabase, playerData.getResult(), add_balance,true);
            PlayerDataSyncPayload.sendTo(player, playerData.getResult().getBalance() + add_balance, playerData.getResult().getJobId());
            player.sendSystemMessage(Component.literal(add_balance + " の報酬を得ました！"));
            return TransactionResult.success("Reward added for breaking block");
        }, 500);
    }

    private void onItemFished(ItemFishedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        String uuid = player.getUUID().toString();
        var transaction = new TransactionExecutor(mongoClient);
        transaction.runTransactionAsync("game", (db, session) -> {
            var playerData = UserGamePlayerCollectionDb.findPlayerGameDataOne(gameDatabase, uuid);
            if (playerData.getResult() == null) return TransactionResult.failure("Player not found");

            int jobId = playerData.getResult().getJobId();
            double reward = 25.0;
            double sellRatio = getSellRatio(jobId);
            if (jobId == 5) {
                reward *= 1.5;
            }
            double finalReward = reward * sellRatio;

            UserGamePlayerCollectionDb.addBalance(gameDatabase, playerData.getResult(), finalReward,true);
            player.sendSystemMessage(Component.literal("釣りをして " + finalReward + " の報酬を得ました！"));
            return TransactionResult.success("Reward added for fishing");
        });
    }

    // Crafting table event
    private void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return; // getEntity() を使用
        if (event.getInventory().getContainerSize() != 9) return; // 作業台クラフトのみ

        boolean canCraft = PlayerClientData.jobId == 2;
        var crafted = event.getCrafting();
        if (!canCraft) {
            // 結果アイテムを無効化（クラフトされないようにする）
            crafted.setCount(0); // または event.getEntity().drop(...) して、クラフトからは削除
            var container = event.getInventory();
            // 材料を返す（必要に応じて）
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack ingredient = container.getItem(i);
                if (!ingredient.isEmpty()) {
                    player.getInventory().add(ingredient.copy());
                    container.setItem(i, ItemStack.EMPTY);
                }
            }
            player.sendSystemMessage(Component.literal("クラフト失敗"));
        }
    }

    // Potion brewing event


    // Enchanting table interaction event
    private void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getLevel().getBlockState(event.getPos()).getBlock() == Blocks.ENCHANTING_TABLE) {
            boolean canEnchant = checkEnchantingConditions(player);
            if (!canEnchant) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.literal("エンチャントテーブルを使用できません！"));
            }
        }
        else  if (event.getLevel().getBlockState(event.getPos()).getBlock() == Blocks.BREWING_STAND) {
            boolean canBrew = checkBrewingConditions(player);
            if (!canBrew) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.literal("ポーションを醸造できません！"));
            }
        }
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.CRAFTING_TABLE)) {
            event.setCanceled(true); // 既存作業台のGUI開きをキャンセル

            // 自前のメニューを開く
            MenuProvider provider = new SimpleMenuProvider(
                    (id, inv, p) -> new LockedCraftingMenu(id, inv, ContainerLevelAccess.create(level, pos), player),
                    Component.literal("Locked Workbench")
            );
            player.openMenu(provider, pos);
        }
    }

    // Draw text on crafting screen (client-side)
    // Draw text on crafting screen (client-side)

    public void onRenderScreen(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof CraftingScreen) {
            int screenWidth = Minecraft.getInstance().getWindow().getWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getHeight();
            Font font = Minecraft.getInstance().font;
            String text = "クラフト中..."; // Custom text
            int textWidth = font.width(text);
            int textHeight = font.lineHeight;
            int x = 10;
            int y = 50;
            var graphic = event.getGuiGraphics();
            graphic.drawString(font, text, 10, 10, 0xFFFFFF);
        }
    }

    // Condition checks (placeholders, customize as needed)


    private boolean checkBrewingConditions(ServerPlayer player) {
        String uuid = player.getUUID().toString();
        var playerData = UserGamePlayerCollectionDb.findPlayerGameDataOne(gameDatabase, uuid);
        if (playerData.getResult() == null) return false;
        int jobId = playerData.getResult().getJobId();
        // Example: Only job ID 2 (平民) can brew potions
        return jobId == 2;
    }

    private boolean checkEnchantingConditions(ServerPlayer player) {
        String uuid = player.getUUID().toString();
        var playerData = UserGamePlayerCollectionDb.findPlayerGameDataOne(gameDatabase, uuid);
        if (playerData.getResult() == null) return false;
        int jobId = playerData.getResult().getJobId();
        // Example: Only job ID 6 (傭兵) can enchant
        return jobId == 6;
    }

    private boolean isWoodBlock(Block block) {
        return Set.of(
                Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG,
                Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG,
                Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG
        ).contains(block);
    }

    private void registerCommands(ServerStartingEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        dispatcher.register(
                Commands.literal("refreshjobcache")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            JobTypeCollectionDb.MemoryCacheJobTypeCollectionData(gameDatabase);
                            context.getSource().sendSuccess(() -> Component.literal("Job type cache refreshed!"), false);
                            return 1;
                        })
        );
    }

    private boolean isOreBlock(Block block) {

        return Set.of(
                Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE,
                Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.LAPIS_ORE,
                Blocks.REDSTONE_ORE, Blocks.COPPER_ORE
        ).contains(block);
    }

    private double getSellRatio(int jobId) {
        if(JobTypeCollectionDb.cache_data == null) return 0.0;
        var jobData = JobTypeCollectionDb.cache_data.stream().filter(item -> item.getJobId() == jobId).findFirst();
        return jobData != null ? jobData.get().getSellRatio() : 0.0;
    }
}