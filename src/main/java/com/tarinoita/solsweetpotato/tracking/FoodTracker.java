package com.tarinoita.solsweetpotato.tracking;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.item.foodcontainer.FoodContainerItem;
import com.tarinoita.solsweetpotato.tracking.benefits.BenefitsHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


@Mod.EventBusSubscriber(modid = SOLSweetPotato.MOD_ID)
public final class FoodTracker {

	@SubscribeEvent
	public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
		if (!BenefitsHandler.checkEvent(event)) {
			return;
		}

		Player player = (Player) event.getEntity();

		Item usedItem = event.getItem().getItem();
		if (!usedItem.isEdible() && usedItem != Items.CAKE) return;
		if (usedItem instanceof FoodContainerItem) return;

		updateFoodList(usedItem, player);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onCakeBlockEaten(PlayerInteractEvent.RightClickBlock event) {
		// Canceled means some other mod already resolved this event,
		// e.g. Farmer's Delight cut off a slice with a knife.
		if (event.isCanceled()) return;

		BlockState state = event.getWorld().getBlockState(event.getPos());
		Block clickedBlock = state.getBlock();
		Player player = event.getPlayer();

		if (player.canEat(false) && event.getHand() == InteractionHand.MAIN_HAND) {
			Item eatenItem = clickedBlock.asItem();

			if (clickedBlock == Blocks.CAKE)
			{
				if (SOLSweetPotato.HasFarmersDelight())
					eatenItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("farmersdelight:cake_slice"));

				assert eatenItem != null;
				updateFoodList(eatenItem, player);
			}

			if (eatenItem.getRegistryName().getNamespace().equals("farmersdelight") && clickedBlock.getClass().getName().contains("PieBlock")) {
				// fetch the slice item to register value for
				eatenItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(eatenItem.getRegistryName().toString() + "_slice"));
				if (eatenItem != null)
					updateFoodList(eatenItem, player);
			}
		}
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