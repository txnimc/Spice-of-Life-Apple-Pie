/**
 * Much of the following code was adapted from Cyclic's storage bag code.
 * Copyright for portions of the code are held by Samson Basset (Lothrazar)
 * as part of Cyclic, under the MIT license.
 */
package com.kevun1.solpotato.item.foodcontainer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FoodContainerCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {
    private int slots;
    private final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> new ItemStackHandler(slots) {

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return !(stack.getItem() instanceof FoodContainerItem) && super.isItemValid(slot, stack);
        }
    });

    public FoodContainerCapabilityProvider(ItemStack stack, int slots) {
        this.slots = slots;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return inventory.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (inventory.isPresent()) {
            return inventory.resolve().get().serializeNBT();
        }
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        inventory.ifPresent(h -> h.deserializeNBT(nbt));
    }
}