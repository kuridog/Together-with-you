package com.kuridog.twu.entity;

import com.kuridog.twu.Togetherwithyou;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Togetherwithyou.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<PetEntity>> WOLF_PET =
            ENTITY_TYPES.register("wolf_pet", () ->
                    EntityType.Builder.of(PetEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .noSave()
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .build("wolf_pet"));
}