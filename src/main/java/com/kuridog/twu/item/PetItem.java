package com.kuridog.twu.item;

import com.kuridog.twu.component.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.kuridog.twu.component.ModDataComponents.SUMMONED;

public class PetItem extends Item {

    private static final int BUFF_DURATION = 20 * 12;   // 12秒

    public PetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            boolean summoned = stack.getOrDefault(SUMMONED.get(), false);
            stack.set(SUMMONED.get(), !summoned);

            if (summoned) {
                player.sendSystemMessage(Component.literal("ペットを戻した"));
            } else {
                player.sendSystemMessage(Component.literal(player.getName().getString() + "はペットを召喚した！"));
            }

            player.getCooldowns().addCooldown(this, 20);   // 連打防止に1秒
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide()) {
            return;
        }
        if (!(entity instanceof Player player)) {
            return;
        }
        if (!stack.getOrDefault(SUMMONED.get(), false)) {
            return;
        }

        // 1秒に1回だけバフをかけ直す
        if (level.getGameTime() % 20 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, BUFF_DURATION, 0));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, BUFF_DURATION, 0));

            if (player.getHealth() <= player.getMaxHealth() / 2) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, BUFF_DURATION, 0));
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(SUMMONED.get(), false);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.getOrDefault(SUMMONED.get(), true)) {
            tooltipComponents.add(Component.literal("状態：召喚中"));
        } else {
            tooltipComponents.add(Component.literal("状態：待機中"));
        }
    }
}
