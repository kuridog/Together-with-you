package com.kuridog.twu.entity;


import com.kuridog.twu.item.ModItems;
import com.kuridog.twu.item.PetItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class PetEntity extends Entity implements ItemSupplier {
    private UUID ownerUUID;

    private static final double ATTACK_RANGE = 8.0;      // 攻撃範囲（ブロック）
    private static final int ATTACK_INTERVAL = 20 * 2;   // 攻撃の間隔（2秒）
    private static final float ATTACK_DAMAGE = 4.0f;     // ダメージ（ハート2個分）

    private int attackCooldown = 0;

    public PetEntity(EntityType<? extends PetEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;      // ブロックをすり抜ける
        this.setNoGravity(true);    // 落ちない
    }

    public void setOwner(Player player) {
        this.ownerUUID = player.getUUID();
    }


    public static boolean exists(Player player) {
        return !player.level().getEntitiesOfClass(PetEntity.class,
                player.getBoundingBox().inflate(32),
                pet -> pet.isOwnedBy(player)).isEmpty();
    }

    public Player getOwner() {
        if (ownerUUID == null) {
            return null;
        }
        return level().getPlayerByUUID(ownerUUID);
    }

    public boolean isOwnedBy(Player player) {
        return player.getUUID().equals(ownerUUID);
    }

    private void attack(Monster target, Player owner) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 from = position();
        Vec3 to = target.position().add(0, target.getBbHeight() / 2, 0);

        // ① ペットから敵まで、光の線を引く
        for (int i = 0; i <= 15; i++) {
            Vec3 point = from.lerp(to, i / 15.0);
            serverLevel.sendParticles(ParticleTypes.CRIT, point.x, point.y, point.z, 1, 0, 0, 0, 0);
        }

        // ② 当たった場所に火花
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, to.x, to.y, to.z, 3, 0.3, 0.3, 0.3, 0.1);

        // ③ 音
        level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.WOLF_GROWL, SoundSource.NEUTRAL, 0.3f, 1.5f + random.nextFloat() * 0.3f);

        // ④ ダメージ（持ち主の攻撃として扱う）
        target.hurt(damageSources().playerAttack(owner), ATTACK_DAMAGE);
    }

    private Monster findTarget(Player owner) {
        List<Monster> monsters = level().getEntitiesOfClass(Monster.class,
                owner.getBoundingBox().inflate(ATTACK_RANGE),
                monster -> monster.isAlive());

        Monster nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Monster monster : monsters) {
            double distance = monster.distanceToSqr(owner);
            if (distance < nearestDistance) {
                nearest = monster;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            return;
        }

        // 持ち主がいない・倒れた・召喚中のアイテムを持っていない → 消える
        Player owner = getOwner();
        if (owner == null || !owner.isAlive() || !PetItem.hasSummonedPet(owner)) {
            discard();
            return;
        }

        // 目標の位置：持ち主の左後ろ、頭の高さ
        Vec3 offset = new Vec3(0.8, 0, -0.8).yRot(-owner.yBodyRot * Mth.DEG_TO_RAD);
        double bob = Math.sin(tickCount * 0.1) * 0.15;
        Vec3 followPos = owner.position().add(offset.x, 1.6 + bob, offset.z);

        // 今の位置から、目標に向かって15%だけ近づく
        Vec3 next = position().lerp(followPos, 0.15);
        setPos(next.x, next.y, next.z);

        // 攻撃
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        Monster target = findTarget(owner);
        if (target != null) {
            attack(target, owner);
            attackCooldown = ATTACK_INTERVAL;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(ModItems.WOLF_PET.get());
    }

}