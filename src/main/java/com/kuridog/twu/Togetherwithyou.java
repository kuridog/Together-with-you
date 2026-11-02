package com.kuridog.twu;

import com.kuridog.twu.component.ModDataComponents;
import com.kuridog.twu.entity.ModEntities;
import com.kuridog.twu.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(Togetherwithyou.MODID)
public class Togetherwithyou {
    public static final String MODID = "kuri_twu";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // クリエイティブタブ（ID: kuri_twu:pet_tab）
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PET_TAB =
            CREATIVE_MODE_TABS.register("pet_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.kuri_twu"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.WOLF_PET.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.WOLF_PET.get());
                    }).build());

    public Togetherwithyou(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // 登録係をNeoForgeに渡す
        ModItems.ITEMS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}