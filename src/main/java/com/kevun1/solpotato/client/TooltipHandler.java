package com.kevun1.solpotato.client;

import com.kevun1.solpotato.SOLPotato;
import com.kevun1.solpotato.SOLPotatoConfig;
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
			if (hasBeenEaten) {
				tooltip.add(localizedTooltip("disabled.eaten", TextFormatting.DARK_RED));
			}
			String key = SOLPotatoConfig.hasWhitelist() ? "whitelist" : "blacklist";
			tooltip.add(localizedTooltip("disabled." + key, TextFormatting.DARK_GRAY));
		} else {
			if (hasBeenEaten) {
				tooltip.add(localizedTooltip("cheap.eaten", TextFormatting.DARK_RED));
			}
			tooltip.add(localizedTooltip("cheap", TextFormatting.DARK_GRAY));
		}
	}
	
	private static ITextComponent localizedTooltip(String path, TextFormatting color) {
		return localizedComponent("tooltip", path).modifyStyle(style -> style.applyFormatting(color));
	}
	
	private TooltipHandler() {}
}
