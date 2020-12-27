package com.cazsius.solcarrot.tracking;

import com.cazsius.solcarrot.SOLCarrotConfig;
import com.cazsius.solcarrot.api.FoodCapability;
import com.cazsius.solcarrot.api.SOLCarrotAPI;
import com.cazsius.solcarrot.client.gui.BenefitsPage;
import com.cazsius.solcarrot.tracking.benefits.BenefitsHandler;
import javafx.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.*;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public final class FoodList implements FoodCapability {
	private static final String NBT_KEY_FOOD_LIST = "foodList";
	private static final String NBT_KEY_UNIQUE_FOOD = "food";
	private static final String NBT_KEY_LAST_EATEN = "lastEaten";
	
	public static FoodList get(PlayerEntity player) {
		return (FoodList) player.getCapability(SOLCarrotAPI.foodCapability)
			.orElseThrow(FoodListNotFoundException::new);
	}

	// Keys - foods eaten, Values - lastEaten, i.e. # meals ago the food was last eaten
	private final Map<FoodInstance, Integer> uniqueFoods = new HashMap<>();

	@Nullable
	private ProgressInfo cachedProgressInfo;
	
	public FoodList() {}
	
	private final LazyOptional<FoodList> capabilityOptional = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		return capability == SOLCarrotAPI.foodCapability ? capabilityOptional.cast() : LazyOptional.empty();
	}

	@Nullable
	private CompoundNBT serializeUniqueFood(Map.Entry<FoodInstance, Integer> foodPair) {
		FoodInstance food = foodPair.getKey();
		Integer lastEaten = foodPair.getValue();

		String encodedFood = food.encode();
		if (encodedFood == null) {
			return null;
		}

		CompoundNBT tag = new CompoundNBT();
		StringNBT s = StringNBT.valueOf(encodedFood);
		FloatNBT i = FloatNBT.valueOf(lastEaten);
		tag.put(NBT_KEY_UNIQUE_FOOD, s);
		tag.put(NBT_KEY_LAST_EATEN, i);

		return tag;
	}
	
	/** used for persistent storage */
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		
		ListNBT list = new ListNBT();

		uniqueFoods.entrySet().stream()
			.map(this::serializeUniqueFood)
			.filter(Objects::nonNull)
			.forEach(list::add);
		tag.put(NBT_KEY_FOOD_LIST, list);

//		foods.stream()
//			.map(food->food.encode())
//			.filter(Objects::nonNull)
//			.map(StringNBT::valueOf)
//			.forEach(list::add);
//		tag.put(NBT_KEY_FOOD_LIST, list);
		
		return tag;
	}

	@Nullable
	private Pair<FoodInstance, Integer> deserializeUniqueFood(Pair<String, Float> encoded) {
		FoodInstance uniqueFood = FoodInstance.decode(encoded.getKey());
		Integer lastEaten = Math.round(encoded.getValue());

		if (uniqueFood == null)
			return null;

		return new Pair<>(uniqueFood, lastEaten);
	}

	/** used for persistent storage */
	@Override
	public void deserializeNBT(CompoundNBT tag) {
		ListNBT list = tag.getList(NBT_KEY_FOOD_LIST, Constants.NBT.TAG_COMPOUND);

		uniqueFoods.clear();
		list.stream()
			.map(nbt-> (CompoundNBT) nbt)
			.map(nbt -> new Pair<>(nbt.getString(NBT_KEY_UNIQUE_FOOD), nbt.getFloat(NBT_KEY_LAST_EATEN)))
			.map(this::deserializeUniqueFood)
			.filter(Objects::nonNull)
			.forEach(pair -> uniqueFoods.put(pair.getKey(), pair.getValue()));

		invalidateProgressInfo();
//		ListNBT list = tag.getList(NBT_KEY_FOOD_LIST, Constants.NBT.TAG_STRING);
//
//		foods.clear();
//		list.stream()
//			.map(nbt -> (StringNBT) nbt)
//			.map(StringNBT::getString)
//			.map(FoodInstance::decode)
//			.filter(Objects::nonNull)
//			.forEach(foods::add);
//
//		invalidateProgressInfo();
	}
	
	/** @return true if the food was not previously known, i.e. if a new food has been tried */
	public boolean addFood(Item food) {
		if (!SOLCarrotConfig.shouldCount(food)) {
			return false;
		}

		ArrayList<FoodInstance> toRemove = new ArrayList<>();

		for (Map.Entry<FoodInstance, Integer> entry : uniqueFoods.entrySet()) {
			FoodInstance foodInstance = entry.getKey();
			Integer lastEaten = entry.getValue();

			lastEaten++;
			uniqueFoods.put(foodInstance, lastEaten);

			if (lastEaten >= SOLCarrotConfig.size()) {
				toRemove.add(foodInstance);
			}
		}

		for (FoodInstance foodInstance : toRemove) {
			uniqueFoods.remove(foodInstance);
		}

		FoodInstance newlyEaten = new FoodInstance(food);
		uniqueFoods.put(newlyEaten, 0);

		System.out.println("LIST: " + uniqueFoods.entrySet());

		invalidateProgressInfo();
		return true;
	}

	@Override
	public double foodDiversity() {
		return foodDiversity(uniqueFoods.entrySet());
	}

	public static double foodDiversity(Set<Map.Entry<FoodInstance, Integer>> foodData) {
		double diversity = 0;

		for (Map.Entry<FoodInstance, Integer> entry : foodData) {
			FoodInstance food = entry.getKey();
			Integer lastEaten = entry.getValue();

			diversity += calculateDiversityContribution(food, lastEaten);
		}

		return diversity;
	}

	public static double calculateDiversityContribution(FoodInstance food, int lastEaten) {
		// replace 1 with value according to complexity
		return calculateTimePenalty(lastEaten) * 1;
	}

	private static double calculateTimePenalty(int lastEaten) {
		// store size somewhere?
		int size = SOLCarrotConfig.size();
		return (size - lastEaten) / (double) size;
	}

	public Set<Map.Entry<FoodInstance, Integer>> getData() {
		return uniqueFoods.entrySet();
	}

	@Override
	public boolean hasEaten(Item food) {
		if (!food.isFood()) return false;
		return uniqueFoods.containsKey(new FoodInstance(food));
//		return foods.contains(new FoodInstance(food));
	}
	
	public void clearFood() {
//		foods.clear();
		uniqueFoods.clear();
		invalidateProgressInfo();
	}
	
	public Set<FoodInstance> getEatenFoods() {
//		return new HashSet<>(foods);
		return uniqueFoods.keySet();
	}
	
	// TODO: is this actually desirable? it doesn't filter at all
	@Override
	public int getEatenFoodCount() {
//		return foods.size();
		return uniqueFoods.size();
	}
	
	public ProgressInfo getProgressInfo() {
		if (cachedProgressInfo == null) {
			cachedProgressInfo = new ProgressInfo(this);
		}
		return cachedProgressInfo;
	}
	
	public void invalidateProgressInfo() {
		cachedProgressInfo = null;
	}
	
	public static final class Storage implements Capability.IStorage<FoodCapability> {
		@Override
		public INBT writeNBT(Capability<FoodCapability> capability, FoodCapability instance, Direction side) {
			return instance.serializeNBT();
		}
		
		@Override
		public void readNBT(Capability<FoodCapability> capability, FoodCapability instance, Direction side, INBT tag) {
			instance.deserializeNBT((CompoundNBT) tag);
		}
	}
	
	public static class FoodListNotFoundException extends RuntimeException {
		public FoodListNotFoundException() {
			super("Player must have food capability attached, but none was found.");
		}
	}
}
