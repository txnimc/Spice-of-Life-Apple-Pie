package com.cazsius.solcarrot.client.gui.elements;

import com.cazsius.solcarrot.tracking.FoodInstance;
import com.cazsius.solcarrot.tracking.FoodList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.util.ITooltipFlag.TooltipFlags.NORMAL;

import static com.cazsius.solcarrot.lib.Localization.localized;

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
        ITextComponent foodName = itemStack.getTooltip(mc.player, NORMAL).get(0);
        List<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(foodName);

        double contribution = FoodList.calculateDiversityContribution(new FoodInstance(itemStack.getItem()), lastEaten);

        tooltip.add(new StringTextComponent(localized("gui", "food_book.queue.tooltip.contribution_label")
            + ": " + String.format("%.2f", contribution)).mergeStyle(TextFormatting.GRAY));
        tooltip.add(new StringTextComponent(localized("gui", "food_book.queue.tooltip.last_eaten_label")
            + ": " + lastEaten + " " + localized("gui", "food_book.queue.tooltip.last_eaten_label_2"))
            .mergeStyle(TextFormatting.GRAY));

        return tooltip;
    }
}
