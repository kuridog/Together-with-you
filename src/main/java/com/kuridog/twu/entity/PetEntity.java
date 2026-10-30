package com.kuridog.twu.entity;


import com.kuridog.twu.item.ModItems;
import com.kuridog.twu.item.PetItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class PetEntity extends Entity implements ItemSupplier {
    private UUID ownerUUID;

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
        Vec3 target = owner.position().add(offset.x, 1.6 + bob, offset.z);

        // 今の位置から、目標に向かって15%だけ近づく
        Vec3 next = position().lerp(target, 0.15);
        setPos(next.x, next.y, next.z);
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