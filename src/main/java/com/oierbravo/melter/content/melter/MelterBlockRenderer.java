package com.oierbravo.melter.content.melter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class MelterBlockRenderer implements BlockEntityRenderer<MelterBlockEntity> {
    public MelterBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MelterBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        FluidStack fluidStack = pBlockEntity.getFluidHandler().getFluidInTank(0);
        if(!fluidStack.isEmpty()){
            int amount = fluidStack.getAmount();
            int total = pBlockEntity.getFluidHandler().getTankCapacity(0);
            this.renderFluidInTank(pBlockEntity.getLevel(), pBlockEntity.getBlockPos(), fluidStack, pPoseStack, pBufferSource, (amount / (float) total));
        }
    }
    private void renderFluidInTank(BlockAndTintGetter world, BlockPos pos, FluidStack fluidStack, PoseStack matrix, MultiBufferSource buffer, float percent) {
        matrix.pushPose();
        matrix.translate(0.5d, 0.5d, 0.5d);
        Matrix4f matrix4f = matrix.last().pose();
        Matrix3f matrix3f = matrix.last().normal();

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite fluidTexture = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(clientFluid.getStillTexture(fluidStack));

        int color = clientFluid.getTintColor(fluidStack);

        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        this.renderTopFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        matrix.popPose();

    }

    private void renderTopFluidFace(TextureAtlasSprite sprite, Matrix4f matrix4f, Matrix3f normalMatrix, VertexConsumer builder, int color, float percent) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float width = 10 / 16f;
        float height = 14 / 16f;

        float minU = sprite.getU(4);
        float maxU = sprite.getU(16);
        float minV = sprite.getV(4);
        float maxV = sprite.getV(16);

        float pY = -height / 2 + percent * height;

        builder.vertex(matrix4f, -width / 2, pY , -width / 2).color(r, g, b, a)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, -width / 2, pY, width / 2).color(r, g, b, a)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, width / 2, pY, width / 2).color(r, g, b, a)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, width / 2, pY, -width / 2).color(r, g, b, a)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

}
