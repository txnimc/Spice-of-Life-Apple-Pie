package com.tarinoita.solsweetpotato.item.foodcontainer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import com.tarinoita.solsweetpotato.lib.Localization;

import javax.annotation.Nullable;

public class FoodContainerProvider implements MenuProvider {
    private String displayName;

    public FoodContainerProvider(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(Localization.keyString("item", "container." + displayName));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new FoodContainer(i, playerInventory, player);
    }
}
