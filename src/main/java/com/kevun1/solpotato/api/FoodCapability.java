package com.kevun1.solpotato.api;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 Provides a stable, (strongly) simplified view of the food list.
 */
public interface FoodCapability extends ICapabilitySerializable<CompoundNBT> {
	/**
	 @return whether or not the given food is being tracked.
	 */
	boolean hasEaten(Item item);

	/**
	 * @return the food diversity score of the current food queue.
	 */
	double foodDiversity();
}
