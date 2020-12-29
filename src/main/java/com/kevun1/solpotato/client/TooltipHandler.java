package com.kevun1.solpotato.client;

import com.kevun1.solpotato.SOLPotato;
import com.kevun1.solpotato.SOLPotatoConfig;
import com.kevun1.solpotato.tracking.FoodInstance;
import com.kevun1.solpotato.tracking.FoodList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.kevun1.solpotato.lib.Localization.localized;
import static com.kevun1.solpotato.lib.Localization.localizedComponent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SOLPotato.MOD_ID)
public final class TooltipHandler {
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		if (!SOLPotatoConfig.isFoodTooltipEnabled()) return;
		
		PlayerEntity player = event.getPlayer();
		if (player == null) return;
		
		Item food = event.getItemStack().getItem();
		if (!food.isFood()) return;
		
		FoodList foodList = FoodList.get(player);
		boolean hasBeenEaten = foodList.hasEaten(food);
		boolean isAllowed = SOLPotatoConfig.isAllowed(food);

		List<ITextComponent> tooltip = event.getToolTip();
		if (!isAllowed) {
			tooltip.add(localizedTooltip("disabled", TextFormatting.DARK_GRAY));
		} else {
			if (hasBeenEaten) {
				int lastEaten = foodList.getLastEaten(food);
				double contribution = FoodList.calculateDiversityContribution(new FoodInstance(food), lastEaten);

				addDiversityInfoTooltips(tooltip, contribution, lastEaten);
			}
		}
	}
	
	private static ITextComponent localizedTooltip(String path, TextFormatting color) {
		return localizedComponent("tooltip", path).modifyStyle(style -> style.applyFormatting(color));
	}

	public static List<ITextComponent> addDiversityInfoTooltips(List<ITextComponent> tooltip, double contribution, int lastEaten) {
		String contribution_path = "food_book.queue.tooltip.contribution_label";
		tooltip.add(new StringTextComponent(localized("gui", contribution_path)
				+ ": " + String.format("%.2f", contribution)).mergeStyle(TextFormatting.GRAY));
		String last_eaten_path = "food_book.queue.tooltip.last_eaten_label";
		if (lastEaten == 1) {
			last_eaten_path = "food_book.queue.tooltip.last_eaten_label_singular";
		}
		tooltip.add(new StringTextComponent(localized("gui", last_eaten_path, lastEaten))
				.mergeStyle(TextFormatting.GRAY));
		return tooltip;
	}
	
	private TooltipHandler() {}
}
