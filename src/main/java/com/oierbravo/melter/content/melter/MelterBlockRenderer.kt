package com.oierbravo.melter.content.melter

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Matrix3f
import com.mojang.math.Matrix4f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.fluids.FluidStack

class MelterBlockRenderer(context: BlockEntityRendererProvider.Context?) : BlockEntityRenderer<MelterBlockEntity> {
    override fun render(
        pBlockEntity: MelterBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBufferSource: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val fluidStack = pBlockEntity.fluidHandler.getFluidInTank(0)
        val amount = fluidStack.amount
        val total = pBlockEntity.fluidHandler.getTankCapacity(0)
        val percent = amount / total.toFloat()
        if (!fluidStack.isEmpty) {
            renderFluidInTank(pBlockEntity.level, pBlockEntity.blockPos, fluidStack, pPoseStack, pBufferSource, percent)
        }
        val itemStack = pBlockEntity.itemHandler.getStackInSlot(0)
        if (!itemStack.isEmpty) {
            pPoseStack.pushPose()
            pPoseStack.translate(0.5, 0.8 * percent, 0.5)
            renderBlock(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, itemStack)
            pPoseStack.popPose()
        }

        //FluidRenderer.renderFluidBox(fluidStack,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,pBufferSource,pPoseStack,pPackedLight,false);
    }

    private fun renderFluidInTank(
        world: BlockAndTintGetter?,
        pos: BlockPos,
        fluidStack: FluidStack,
        matrix: PoseStack,
        buffer: MultiBufferSource,
        percent: Float
    ) {
        matrix.pushPose()
        matrix.translate(0.5, 0.5, 0.5)
        val matrix4f = matrix.last().pose()
        val matrix3f = matrix.last().normal()
        val fluid = fluidStack.fluid
        val clientFluid = IClientFluidTypeExtensions.of(fluid)
        val fluidTexture = Minecraft.getInstance()
            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
            .apply(clientFluid.getStillTexture(fluidStack))
        val color = clientFluid.getTintColor(fluidStack)
        val builder = buffer.getBuffer(RenderType.translucent())
        renderTopFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent)
        matrix.popPose()
    }

    private fun renderTopFluidFace(
        sprite: TextureAtlasSprite,
        matrix4f: Matrix4f,
        normalMatrix: Matrix3f,
        builder: VertexConsumer,
        color: Int,
        percent: Float
    ) {
        val r = (color shr 16 and 0xFF) / 255f
        val g = (color shr 8 and 0xFF) / 255f
        val b = (color and 0xFF) / 255f
        val a = (color shr 24 and 0xFF) / 255f
        val width = 10 / 16f
        val height = 14 / 16f
        val minU = sprite.getU(4.0)
        val maxU = sprite.getU(16.0)
        val minV = sprite.getV(4.0)
        val maxV = sprite.getV(16.0)
        val pY = -height / 2 + percent * height
        builder.vertex(matrix4f, -width / 2, pY, -width / 2).color(r, g, b, a)
            .uv(minU, minV)
            .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0f, 1f, 0f)
            .endVertex()
        builder.vertex(matrix4f, -width / 2, pY, width / 2).color(r, g, b, a)
            .uv(minU, maxV)
            .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0f, 1f, 0f)
            .endVertex()
        builder.vertex(matrix4f, width / 2, pY, width / 2).color(r, g, b, a)
            .uv(maxU, maxV)
            .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0f, 1f, 0f)
            .endVertex()
        builder.vertex(matrix4f, width / 2, pY, -width / 2).color(r, g, b, a)
            .uv(maxU, minV)
            .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0f, 1f, 0f)
            .endVertex()
    }

    protected fun renderBlock(ms: PoseStack?, buffer: MultiBufferSource?, light: Int, overlay: Int, stack: ItemStack?) {
        Minecraft.getInstance()
            .itemRenderer
            .renderStatic(stack, ItemTransforms.TransformType.GROUND, light, overlay, ms, buffer, 0)
    }
}