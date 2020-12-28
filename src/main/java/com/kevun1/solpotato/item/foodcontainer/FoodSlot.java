package com.kevun1.solpotato.item.foodcontainer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class FoodSlot extends SlotItemHandler {
    public FoodSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        if (!stack.isFood()) {
            return false;
        }
        return super.isItemValid(stack);
    }
}
