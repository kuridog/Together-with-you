package com.kuridog.twu.client;

import com.kuridog.twu.entity.PetEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

public class PetRenderer extends EntityRenderer<PetEntity> {
    private final ItemRenderer itemRenderer;

    public PetRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PetEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // 向きをなめらかに補間して、その方向に回す
        float yaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.translate(0.0, 0.25, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - yaw));

        poseStack.scale(1f, 1f, 1f);

        // ペットアイテムのモデル（立体の頭）を描く
        itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.FIXED, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PetEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}