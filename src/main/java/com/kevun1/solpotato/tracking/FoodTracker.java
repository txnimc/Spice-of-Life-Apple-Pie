package com.kevun1.solpotato.tracking;

import com.kevun1.solpotato.SOLPotato;
import com.kevun1.solpotato.item.foodcontainer.FoodContainerItem;
import com.kevun1.solpotato.tracking.benefits.BenefitsHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = SOLPotato.MOD_ID)
public final class FoodTracker {

	@SubscribeEvent
	public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
		if (!BenefitsHandler.checkEvent(event)) {
			return;
		}

		Player player = (Player) event.getEntity();
		
		Item usedItem = event.getItem().getItem();
		if (!usedItem.isEdible()) return;
		if (usedItem instanceof FoodContainerItem) return;

		updateFoodList(usedItem, player);
	}

	public static void updateFoodList(Item food, Player player) {
		FoodList foodList = FoodList.get(player);
		foodList.addFood(food);

		double diversity = foodList.foodDiversity();
		BenefitsHandler.updateBenefits(player, diversity);

		CapabilityHandler.syncFoodList(player);
	}

	private FoodTracker() {}
}
