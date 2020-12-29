package com.kevun1.solpotato.item.foodcontainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import com.kevun1.solpotato.lib.Localization;

import javax.annotation.Nullable;

public class FoodContainerProvider implements INamedContainerProvider {
    private String displayName;

    public FoodContainerProvider(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Localization.keyString("item", "container." + displayName));
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
        return new FoodContainer(i, playerInventory, player);
    }
}
