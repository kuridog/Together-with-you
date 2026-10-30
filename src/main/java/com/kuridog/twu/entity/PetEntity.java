package com.kuridog.twu.entity;


import com.kuridog.twu.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PetEntity extends Entity implements ItemSupplier {

    public PetEntity(EntityType<? extends PetEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;      // ブロックをすり抜ける
        this.setNoGravity(true);    // 落ちない
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

        // ふわふわ上下に揺れる
        double bob = Math.sin(tickCount * 0.1) * 0.02;
        this.setPos(getX(), getY() + bob, getZ());
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