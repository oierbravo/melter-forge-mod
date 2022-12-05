package com.oierbravo.melter.network.packets;

import com.oierbravo.melter.content.melter.MelterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemStackSyncS2CPacket {
    private final ItemStack itemStack;
    private final BlockPos pos;

    public ItemStackSyncS2CPacket(ItemStack itemStack, BlockPos pos) {
        this.itemStack = itemStack;
        this.pos = pos;
    }

    public ItemStackSyncS2CPacket(FriendlyByteBuf buf) {
        this.itemStack = buf.readItem();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(itemStack);
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof MelterBlockEntity blockEntity) {
                blockEntity.setItemStack(this.itemStack);

            }
        });
        return true;
    }
}
