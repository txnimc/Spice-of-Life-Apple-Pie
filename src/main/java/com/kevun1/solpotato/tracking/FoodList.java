package com.kevun1.solpotato.tracking;

import com.kevun1.solpotato.ConfigHandler;
import com.kevun1.solpotato.SOLPotatoConfig;
import com.kevun1.solpotato.api.FoodCapability;
import com.kevun1.solpotato.api.SOLPotatoAPI;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
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
		return (FoodList) player.getCapability(SOLPotatoAPI.foodCapability)
			.orElseThrow(FoodListNotFoundException::new);
	}

	// Keys - foods eaten, Values - lastEaten, i.e. # meals ago the food was last eaten
	private final Map<FoodInstance, Integer> uniqueFoods = new HashMap<>();
	
	public FoodList() {}
	
	private final LazyOptional<FoodList> capabilityOptional = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		return capability == SOLPotatoAPI.foodCapability ? capabilityOptional.cast() : LazyOptional.empty();
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

		return tag;
	}

	@Nullable
	private Pair<FoodInstance, Integer> deserializeUniqueFood(Pair<String, Float> encoded) {
		FoodInstance uniqueFood = FoodInstance.decode(encoded.getKey());
		Integer lastEaten = Math.round(encoded.getValue());

		if (uniqueFood == null)
			return null;

		return new ImmutablePair<>(uniqueFood, lastEaten);
	}

	/** used for persistent storage */
	@Override
	public void deserializeNBT(CompoundNBT tag) {
		ListNBT list = tag.getList(NBT_KEY_FOOD_LIST, Constants.NBT.TAG_COMPOUND);

		uniqueFoods.clear();
		list.stream()
			.map(nbt-> (CompoundNBT) nbt)
			.map(nbt -> new ImmutablePair<>(nbt.getString(NBT_KEY_UNIQUE_FOOD), nbt.getFloat(NBT_KEY_LAST_EATEN)))
			.map(this::deserializeUniqueFood)
			.filter(Objects::nonNull)
			.forEach(pair -> uniqueFoods.put(pair.getKey(), pair.getValue()));
	}

	public void addFood(Item food, Map<FoodInstance, Integer> foodMap) {
		if (!SOLPotatoConfig.shouldCount(food) && !SOLPotatoConfig.shouldForbiddenCount()) {
			return;
		}

		ArrayList<FoodInstance> toRemove = new ArrayList<>();

		for (Map.Entry<FoodInstance, Integer> entry : foodMap.entrySet()) {
			FoodInstance foodInstance = entry.getKey();
			Integer lastEaten = entry.getValue();

			lastEaten++;
			foodMap.put(foodInstance, lastEaten);

			if (lastEaten >= SOLPotatoConfig.size()) {
				toRemove.add(foodInstance);
			}
		}

		for (FoodInstance foodInstance : toRemove) {
			foodMap.remove(foodInstance);
		}

		if (SOLPotatoConfig.shouldCount(food)) {
			FoodInstance newlyEaten = new FoodInstance(food);
			foodMap.put(newlyEaten, 0);
		}
	}
	
	public void addFood(Item food) {
		addFood(food, uniqueFoods);
	}

	/**
	 * @return The change in food diversity from eating the food.
	 */
	public double simulateFoodAdd(Item food) {
		if (!SOLPotatoConfig.shouldCount(food) && !SOLPotatoConfig.shouldForbiddenCount()) {
			return 0.0;
		}
		double change = 0.0;

		for (Map.Entry<FoodInstance, Integer> entry : uniqueFoods.entrySet()) {
			FoodInstance foodInstance = entry.getKey();
			Integer lastEaten = entry.getValue();

			double diversityContribution = calculateDiversityContribution(foodInstance, lastEaten);
			lastEaten++;

			if (foodInstance.getItem().equals(food)) {
				change -= diversityContribution;
			}
			else if (lastEaten >= SOLPotatoConfig.size()) {
				change -= diversityContribution;
			}
			else {
				double newDiversityContribution = calculateDiversityContribution(foodInstance, lastEaten);
				change += (newDiversityContribution - diversityContribution);
			}
		}

		if (SOLPotatoConfig.shouldCount(food)) {
			change += calculateDiversityContribution(new FoodInstance(food), 0);
		}

		return change;
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
		return calculateTimePenalty(lastEaten) * getComplexity(food);
	}

	private static double calculateTimePenalty(int lastEaten) {
		int size = SOLPotatoConfig.size();
		int startDecay = SOLPotatoConfig.startDecay();
		int endDecay = SOLPotatoConfig.endDecay();
		double minContribution = SOLPotatoConfig.minContribution();

		if (startDecay > endDecay || startDecay < 0 || endDecay > size ||
				startDecay > size || minContribution > 1 || minContribution < 0) {
			// invalid
			return 0.0;
		}

		if (lastEaten <= startDecay) {
			return 1.0;
		}
		else if (lastEaten > endDecay) {
			return minContribution;
		}

		double slope = (1.0 - minContribution) / (double) (startDecay - endDecay);
		return slope * (lastEaten - startDecay) + 1.0;
	}

	private static double getComplexity(FoodInstance food) {
		Map<FoodInstance, Double> complexityMap = ConfigHandler.complexityMap;
		if (complexityMap == null || !complexityMap.containsKey(food)) {
			return SOLPotatoConfig.defaultContribution();
		}
		return ConfigHandler.complexityMap.get(food);
	}

	public Set<Map.Entry<FoodInstance, Integer>> getData() {
		return uniqueFoods.entrySet();
	}

	public int getLastEaten(Item food) {
		if (!hasEaten(food)) {
			return -1;
		}

		return uniqueFoods.get(new FoodInstance(food));
	}

	@Override
	public boolean hasEaten(Item food) {
		if (!food.isFood()) return false;
		return uniqueFoods.containsKey(new FoodInstance(food));
	}
	
	public void clearFood() {
		uniqueFoods.clear();
	}
	
	public Set<FoodInstance> getEatenFoods() {
		return uniqueFoods.keySet();
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
