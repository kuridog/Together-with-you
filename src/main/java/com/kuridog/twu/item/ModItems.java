package com.kuridog.twu.item;

import com.kuridog.twu.Togetherwithyou;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Togetherwithyou.MODID);

    public static final DeferredItem<PetItem> WOLF_PET = ITEMS.registerItem(
            "wolf_pet",
            properties -> new PetItem(properties),
            new Item.Properties().stacksTo(1));
}