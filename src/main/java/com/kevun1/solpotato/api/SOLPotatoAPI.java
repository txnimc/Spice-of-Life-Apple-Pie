package com.kevun1.solpotato.api;

import com.kevun1.solpotato.tracking.CapabilityHandler;
import com.kevun1.solpotato.tracking.FoodList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 Provides a stable API for interfacing with Spice of Life: Carrot Edition.
 */
public final class SOLPotatoAPI {
	@CapabilityInject(FoodCapability.class)
	public static Capability<FoodCapability> foodCapability;
	
	private SOLPotatoAPI() {}
	
	/**
	 Retrieves the {@link com.kevun1.solpotato.api.FoodCapability} for the given player.
	 */
	public static FoodCapability getFoodCapability(PlayerEntity player) {
		return FoodList.get(player);
	}
	
	/**
	 Synchronizes the food list for the given player to the client, updating their max health in the process.
	 */
	public static void syncFoodList(PlayerEntity player) {
		CapabilityHandler.syncFoodList(player);
	}
}
