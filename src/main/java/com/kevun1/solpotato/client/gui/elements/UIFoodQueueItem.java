package com.kevun1.solpotato.client.gui.elements;

import com.kevun1.solpotato.client.TooltipHandler;
import com.kevun1.solpotato.tracking.FoodInstance;
import com.kevun1.solpotato.tracking.FoodList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.util.ITooltipFlag.TooltipFlags.NORMAL;

import static com.kevun1.solpotato.lib.Localization.localized;

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
    protected void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        List<ITextComponent> tooltip = getFoodQueueTooltip();
        renderTooltip(matrices, itemStack, tooltip, mouseX, mouseY);
    }

    private List<ITextComponent> getFoodQueueTooltip() {
        ITextComponent foodName =  new TranslationTextComponent(itemStack.getItem().getTranslationKey(itemStack))
                .mergeStyle(itemStack.getRarity().color);

        List<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(foodName);

        ITextComponent space = new StringTextComponent("");
        tooltip.add(space);

        double contribution = FoodList.calculateDiversityContribution(new FoodInstance(itemStack.getItem()), lastEaten);

        TooltipHandler.addDiversityInfoTooltips(tooltip, contribution, lastEaten);

        return tooltip;
    }
}
