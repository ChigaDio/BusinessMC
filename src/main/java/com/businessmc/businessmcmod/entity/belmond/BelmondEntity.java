package com.businessmc.businessmcmod.entity.belmond;

import com.businessmc.businessmcmod.BusinessMCMod;
import com.businessmc.businessmcmod.client.ui.menu.BelmondMenu;
import com.businessmc.businessmcmod.entity.constant.EntityConstant;
import com.businessmc.businessmcmod.mongodb.MongoDBConnectionManager;
import com.businessmc.businessmcmod.network.payload.ButtonResultPayload;
import com.businessmc.businessmcmod.util.collection.*;
import com.businessmc.businessmcmod.util.collection.details.RangePos;
import com.businessmc.businessmcmod.util.collection.details.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.common.Mod;

import java.util.ArrayList;
import java.util.Optional;


public class BelmondEntity extends PathfinderMob {
    private  static  final EntityDataAccessor<String> CUSTOM_NAME =
            SynchedEntityData.defineId(BelmondEntity.class,
                    EntityDataSerializers.STRING);

    private static final TagKey<EntityType<?>> INVULNERABLE_TAG =
            TagKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(BusinessMCMod.MOD_ID, "invulnerable"));

    private static final ResourceLocation MENU_PACKET_ID =
            ResourceLocation.fromNamespaceAndPath(BusinessMCMod.MOD_ID, "open_belmond_menu");


    private BlockPos startPos;
    private BlockPos endPos;
    private static final double MOVEMENT_RANGE = 10.0;

    public BelmondEntity(EntityType<? extends PathfinderMob> entityType, Level level) {

        super(entityType, level);

        //データから座標を取得
        var result = EntityCharacterCollectionDb.findIdOne(MongoDBConnectionManager.getInstance().getDatabase("game"),
                EntityConstant.EntityID.BELMOND_ENTITY_ID);

        if(result.isSuccess())
        {

            //座標設定
            Vector3 start = result.getResult().getRangeMove().getStartRange();

            this.startPos = new BlockPos( start.getX().intValue(),start.getY().intValue(),start.getZ().intValue());
            Vector3 end = result.getResult().getRangeMove().getEndRange();

            this.endPos = new BlockPos( end.getX().intValue(),end.getY().intValue(),end.getZ().intValue());
        }
        else
        {
            var add_data = new EntityCharacterCollectionData();
            add_data.setId(EntityConstant.EntityID.BELMOND_ENTITY_ID);
            add_data.setHealth(1000.0);
            add_data.setName("夢の世界のバーのマスター");
            add_data.setType(EntityConstant.EntityMobType.HARMLESS);
            add_data.setIsFixed(true); //特定の場所にしかスポーンしない
            add_data.setIsInvincible(true);
            add_data.setRangeMove(new RangePos());
            add_data.setSpawnPos(new Vector3());
            //DBに一時保存
            EntityCharacterCollectionDb.bulkInsertEntityCharacterCollectionData(MongoDBConnectionManager.getInstance().getDatabase("game"),
                    new ArrayList<>(){{add(add_data);}}
                    );
            this.startPos = this.blockPosition();
            this.endPos = this.startPos.offset(10,0,0);

        }


        this.startPos = this.blockPosition();
        this.endPos = this.startPos.offset(10,0,0);
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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(CUSTOM_NAME,"夢の世界のバーのマスター");

    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.6D, 20, true) {

            @Override
            protected Vec3 getPosition() {
// ランダムなゴール座標を生成（startPosとendPosの範囲内）
                double x = Mth.lerp(random.nextDouble(), startPos.getX(), endPos.getX());
                double z = Mth.lerp(random.nextDouble(), startPos.getZ(), endPos.getZ());
                double y = level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int)x, 0, (int)z)).getY();
                Vec3 goalPos = new Vec3(x, y, z);

                // 現在位置を取得
                Vec3 currentPos = BelmondEntity.this.position();

                // 方向ベクトルを計算
                Vec3 direction = goalPos.subtract(currentPos);

                // 方向ベクトルの長さを計算
                double distance = direction.length();

                // 16ブロック以内ならそのままゴール座標を返す
                if (distance <= 16.0) {
                    return goalPos;
                }

                // 16ブロックを超える場合、方向ベクトルを正規化して15ブロックにスケール
                Vec3 normalizedDirection = direction.normalize(); // 長さを1に正規化
                Vec3 scaledPos = currentPos.add(normalizedDirection.scale(15.0)); // 15ブロック分移動

                // Y座標を再計算（スケール後の座標に合わせて）
                double finalY = level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int)scaledPos.x, 0, (int)scaledPos.z)).getY();
                Vec3 finalPos = new Vec3(scaledPos.x, finalY, scaledPos.z);

                return finalPos;
            }
        });
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.MAX_HEALTH, 20.0D);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {

        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (containerId, inventory, p) -> new BelmondMenu(containerId, inventory, BelmondEntity.this),
                            Component.translatable("")
                    ),
                    buf -> {

                        var result = JobTypeCollectionDb.findAllJobTypeMany(MongoDBConnectionManager.getInstance().getDatabase("game"));


                        buf.writeInt(this.getId());
                        buf.writeInt(result.getResult().size()); // ボタンの数
                        for (var label : result.getResult()) {
                            buf.writeUtf(label.getJobName()); // 各ボタンのラベル
                        }
                        System.out.println("Server: Writing entity ID to buffer: " + this.getId());
                    }
            );
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }


    public boolean isInvulnerableTo(DamageSource source) {
        return this.getType().is(INVULNERABLE_TAG);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        if (name != null) {
            this.entityData.set(CUSTOM_NAME, "夢の世界のバーのマスター");
        }
    }

    @Override
    public Component getCustomName() {
        String name = this.entityData.get(CUSTOM_NAME);
        return name.isEmpty() ? null : Component.literal(name).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }


    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource damageSource) {
        return this.getType().is(INVULNERABLE_TAG) || super.isInvulnerableTo(level,damageSource);
    }




    // サーバーでボタンクリックを処理
    public void handleButtonClick(ServerPlayer player, int buttonIndex) {

        var db = MongoDBConnectionManager.getInstance().getDatabase("game");
        var result = JobTypeCollectionDb.findJobIdOne(db,buttonIndex + 1);
        if(!result.isSuccess()) return;

;

            //職業変換(*本来ならここで手数料かかる処理をするが、今回はなし)

        var update_result = UserGamePlayerCollectionDb.changeJobID(db,player.getStringUUID(),result.getResult().getJobId(),true);
        if(update_result.isSuccess()) {
            ButtonResultPayload.sendToPlayer(player,result.getResult().getJobId());
            player.sendSystemMessage(Component.literal("職業: " + result.getResult().getJobName()));
        }
        else
        {
            player.sendSystemMessage(Component.literal("変更できませんでした"));
        }

    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypes.GENERIC_KILL)) {
            this.setInvulnerable(false);
            return super.hurtServer(level,damageSource, amount);
        }
        return false; // その他のダメージは無効
    }



}
