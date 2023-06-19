package com.tarinoita.solapplepie.client;

import com.tarinoita.solapplepie.SOLApplePie;
import com.tarinoita.solapplepie.SOLApplePieConfig;
import com.tarinoita.solapplepie.tracking.FoodInstance;
import com.tarinoita.solapplepie.tracking.FoodList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.tarinoita.solapplepie.lib.Localization.localized;
import static com.tarinoita.solapplepie.lib.Localization.localizedComponent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SOLApplePie.MOD_ID)
public final class TooltipHandler {
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		if (!SOLApplePieConfig.isFoodTooltipEnabled()) return;
		
		Player player = event.getPlayer();
		if (player == null) return;
		
		Item food = event.getItemStack().getItem();
		if (!food.isEdible()) return;
		
		FoodList foodList = FoodList.get(player);
		boolean hasBeenEaten = foodList.hasEaten(food);
		boolean isAllowed = SOLApplePieConfig.isAllowed(food);

		List<Component> tooltip = event.getToolTip();
		if (!isAllowed) {
			tooltip.add(localizedTooltip("disabled", ChatFormatting.DARK_GRAY));
		} else {
			if (hasBeenEaten) {
				int lastEaten = foodList.getLastEaten(food);
				double contribution = FoodList.calculateDiversityContribution(new FoodInstance(food), lastEaten);

				addDiversityInfoTooltips(tooltip, contribution, lastEaten);
			}
			else {
				addDiversityInfoTooltips(tooltip, FoodList.getComplexity(new FoodInstance(food)), -1);
			}
		}
	}
	
	private static Component localizedTooltip(String path, ChatFormatting color) {
		return localizedComponent("tooltip", path).withStyle(style -> style.applyFormat(color));
	}

	public static List<Component> addDiversityInfoTooltips(List<Component> tooltip, double contribution, int lastEaten) {
		String contribution_path = lastEaten == -1 ? "food_book.queue.tooltip.uneaten_contribution_label" : "food_book.queue.tooltip.contribution_label";
		tooltip.add(new TextComponent(localized("gui", contribution_path) + ": " + String.format("%.2f", contribution)).withStyle(lastEaten == -1 ? ChatFormatting.GRAY : ChatFormatting.GOLD));

		if (lastEaten != -1)
		{
			String last_eaten_path = "food_book.queue.tooltip.last_eaten_label";
			if (lastEaten == 1)
			{
				last_eaten_path = "food_book.queue.tooltip.last_eaten_label_singular";
			}
			tooltip.add(new TextComponent(localized("gui", last_eaten_path, lastEaten)).withStyle(ChatFormatting.GRAY));
		}

		return tooltip;
	}
	
	private TooltipHandler() {}
}
