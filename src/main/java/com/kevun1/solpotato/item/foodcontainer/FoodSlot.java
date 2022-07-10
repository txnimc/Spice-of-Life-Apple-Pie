package com.kevun1.solpotato.item.foodcontainer;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class FoodSlot extends SlotItemHandler {
    public FoodSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        if (!stack.isEdible()) {
            return false;
        }
        return super.mayPlace(stack);
    }
}
