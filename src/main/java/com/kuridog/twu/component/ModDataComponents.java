package com.kuridog.twu.component;

import com.kuridog.twu.Togetherwithyou;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Togetherwithyou.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SUMMONED =
            DATA_COMPONENTS.registerComponentType("summoned",
                    builder -> builder
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL));
}