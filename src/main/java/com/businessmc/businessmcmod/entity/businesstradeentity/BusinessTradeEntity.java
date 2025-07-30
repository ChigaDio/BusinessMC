package com.businessmc.businessmcmod.entity.businesstradeentity;

import com.businessmc.businessmcmod.client.ui.menu.BelmondMenu;
import com.businessmc.businessmcmod.client.ui.menu.provider.BusinessTradeMenuProvider;
import com.businessmc.businessmcmod.entity.belmond.BelmondEntity;
import com.businessmc.businessmcmod.mongodb.MongoDBConnectionManager;
import com.businessmc.businessmcmod.util.collection.BusinessShopCollectionData;
import com.businessmc.businessmcmod.util.collection.BusinessShopCollectionDb;
import com.businessmc.businessmcmod.util.collection.JobTypeCollectionDb;
import com.businessmc.businessmcmod.util.collection.details.ShopItem;
import com.businessmc.businessmcmod.util.collection.details.Vector3;
import com.businessmc.businessmcmod.util.general.UtilGeneral;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;
import net.minecraft.world.damagesource.DamageTypes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BusinessTradeEntity extends PathfinderMob implements IEntityWithComplexSpawn {
    private List<Item> buyable = new ArrayList<>();
    private List<Item> sellable = new ArrayList<>();
    private Vector3 fixedpos = new Vector3();

    public static final EntityDataAccessor<Integer> ENTITY_ID =
            SynchedEntityData.defineId(BusinessTradeEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Vector3f> FIXED_POS =
            SynchedEntityData.defineId(BusinessTradeEntity.class, EntityDataSerializers.VECTOR3);

    public BusinessTradeEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
        this.setInvulnerable(true);
        this.setPersistenceRequired();
    }
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;  // 絶対に遠距離デスポーンしない
    }
    @Override
    public boolean isPersistenceRequired() {
        return true; // 永続性を要求する
    }
    // ダメージ処理をオーバーライド

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D) // 例: 最大HPを20に設定
                .add(Attributes.MOVEMENT_SPEED, 0.25D) // 例: 移動速度を設定

                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D) ;// ノックバック耐性を100%に設定


    }

    // ② 同期データの定義
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ENTITY_ID, 0);
        builder.define(FIXED_POS, new Vector3f(0, 0, 0));

    }

    public  void RenderInitList()
    {
        //ここで初期化
        //クライアント処理
        if(this.level().isClientSide())
        {

            this.buyable = new ArrayList<>();
            this.sellable = new ArrayList<>();
            var database = MongoDBConnectionManager.getInstance().getDatabase("game");
            //データベースから取得
            var result = BusinessShopCollectionDb.findIdOne(database,this.getEntityData().get(ENTITY_ID));
            //存在しなかったら作る
            if(result.isSuccess())
            {


                var data = result.getResult();

                for(var add : data.getBuyList())
                {
                    buyable.add(UtilGeneral.getItemFromName(add.getItemIdName()));
                }
                for(var add : data.getSaleList())
                {
                    sellable.add(UtilGeneral.getItemFromName(add.getItemIdName()));
                }

                var pos = new Vector3();
                pos.setX(this.getX());
                pos.setY(this.getY());
                pos.setX(this.getZ());
                this.fixedpos = pos;


                fixedpos = data.getFixedPos();
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.6D, 20, true) {

            @Override
            protected Vec3 getPosition() {
                double x =fixedpos.getX();
                double z = fixedpos.getZ();
                double y = level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos((int)x, 0, (int)z)).getY();
                return new Vec3(x, y, z);
            }
        });



    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData existingData
    )
    {
        var data = super.finalizeSpawn(world,difficulty,entitySpawnReason,existingData);


        return data;
    }


    /** 外部から ID を取得 */
    public int getBusinessEntityId() {
        return this.getEntityData().get(ENTITY_ID);
    }

    // ③ セーブ／ロード時に永続化
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("business_id", this.getBusinessEntityId());
        output.putDouble("fixed_x", this.getX());
        output.putDouble("fixed_y", this.getY());
        output.putDouble("fixed_z", this.getZ());

    }



    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        int loaded = input.getIntOr("business_id", 0);

        double x = input.getDoubleOr("fixed_x",0.0);
        double y = input.getDoubleOr("fixed_y",0.0);
        double z = input.getDoubleOr("fixed_z",0.0);

        this.getEntityData().set(ENTITY_ID, loaded);

        this.getEntityData().set(FIXED_POS, new Vector3f((float) x, (float) y, (float) z));


    }

    // ④ スポーン時の初回同期（クライアントが受け取る）
    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.getBusinessEntityId());

        if(level().isClientSide())
        {

        }
        else
        {
            var database = MongoDBConnectionManager.getInstance().getDatabase("game");
            //データベースから取得
            var result = BusinessShopCollectionDb.findIdOne(database,this.getEntityData().get(ENTITY_ID));
            //存在しなかったら作る
            if(!result.isSuccess())
            {
                var add_data = new BusinessShopCollectionData();
                add_data.setId(this.getEntityData().get(ENTITY_ID));
                add_data.setBaseRate(1.0);
                var fixed_pos = this.getEntityData().get(FIXED_POS);
                var pos = new Vector3();
                pos.setX((double)fixed_pos.x);
                pos.setY((double)fixed_pos.y);
                pos.setX((double)fixed_pos.z);
                add_data.setFixedPos(pos);
                add_data.setShopName("テスト");
                add_data.setTraderName("おじ");
                add_data.setAccessJobList(new ArrayList<>());

                var add_shop_sale = new ShopItem();
                add_shop_sale.setItemIdName("dirt");
                add_shop_sale.setPrice(1.0);
                add_shop_sale.setPriceRate(1.0);
                add_shop_sale.setIsBasePriceCalculation(false);

                add_data.setSaleList(new ArrayList<>() {{
                    add(add_shop_sale);
                }});
                add_data.setBuyList(new ArrayList<>() {{
                    add(add_shop_sale);
                }});

                for(int count = 0; count < 1; count ++) {

                    add_data.getSaleList().add(add_shop_sale);
                    add_data.getBuyList().add(add_shop_sale);

                }

                BusinessShopCollectionDb.bulkInsertBusinessShopCollectionData(database,new ArrayList<>(){{add(add_data);}});
            }
        }
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {
        int id = buf.readInt();
        this.getEntityData().set(ENTITY_ID, id);


    }

    public List<Item> getBuyableItems() {
        return buyable;
    }

    public List<Item> getSellableItems() {
        return sellable;
    }

    public void processTransaction(ServerPlayer player, Item item, int qty) {
        if (sellable.contains(item)) {
            int removed = removeFromInventory(player, item, qty);
            addCurrency(player, removed * getSellPrice(item));
        } else if (buyable.contains(item)) {
            long price = getBuyPrice(item) * qty;
            if (removeCurrency(player, price)) {
                giveToInventory(player, new ItemStack(item, qty));
            }
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {

        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new BusinessTradeMenuProvider(this),buf ->
                {
                    buf.writeInt(this.getId());
                });
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private int removeFromInventory(Player player, Item item, int qty) {
        int count = 0;
        Inventory inv = player.getInventory();
        int size = inv.getContainerSize();
        for (int i = 0; i < size; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == item && count < qty) {
                int take = Math.min(stack.getCount(), qty - count);
                stack.shrink(take);
                count += take;
            }
        }
        return count;
    }

    private void giveToInventory(Player player, ItemStack stack) {
        player.getInventory().add(stack);
    }

    private boolean removeCurrency(Player player, long amount) {
        // プレイヤーの通貨を減算する機能。Cap に応じて実装。
        return true;
    }

    private void addCurrency(Player player, long amount) {
        // プレイヤーに通貨を追加する機能。
    }




    private long getSellPrice(Item item) { return 1; }
    private long getBuyPrice(Item item) { return 1; }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypes.GENERIC_KILL)) {
            this.setInvulnerable(false);
            return super.hurtServer(level,damageSource, amount);
        }
        return super.hurtServer(level,damageSource, 0.0F); // その他のダメージは無効
    }

}
