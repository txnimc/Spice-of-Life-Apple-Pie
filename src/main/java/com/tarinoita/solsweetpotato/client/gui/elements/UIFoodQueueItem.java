package com.tarinoita.solsweetpotato.client.gui.elements;

import com.tarinoita.solsweetpotato.client.TooltipHandler;
import com.tarinoita.solsweetpotato.tracking.FoodInstance;
import com.tarinoita.solsweetpotato.tracking.FoodList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;


/**
 * Renders an ItemStack representing a food in the FoodList. Has a unique tooltip that displays that food item's
 * contribution to the food diversity.
 */
public class UIFoodQueueItem extends UIItemStack{
    private final int lastEaten;

    public UIFoodQueueItem(ItemStack itemStack, int lastEaten) {
        super(itemStack);

        this.lastEaten = lastEaten;
    }

    @Override
    protected void renderTooltip(PoseStack matrices, int mouseX, int mouseY) {
        List<Component> tooltip = getFoodQueueTooltip();
        renderTooltip(matrices, itemStack, tooltip, mouseX, mouseY);
    }

    private List<Component> getFoodQueueTooltip() {
        Component foodName =  new TranslatableComponent(itemStack.getItem().getDescriptionId(itemStack))
                .withStyle(itemStack.getRarity().color);

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(foodName);

        Component space = new TextComponent("");
        tooltip.add(space);

        double contribution = FoodList.calculateDiversityContribution(new FoodInstance(itemStack.getItem()), lastEaten);

        TooltipHandler.addDiversityInfoTooltips(tooltip, contribution, lastEaten);

        return tooltip;
    }
}
