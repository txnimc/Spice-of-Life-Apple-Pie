package com.kevun1.solpotato.utils;

import com.kevun1.solpotato.SOLPotato;
import com.kevun1.solpotato.tracking.FoodInstance;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplexityParser {
    public static Map<FoodInstance, Double> parse(List<String> unparsed) {
        Map<FoodInstance, Double> complexityMap = new HashMap<>();

        for (String complexityString : unparsed) {
            String[] s = complexityString.split(",", 0);
            if (s.length != 2) {
                SOLPotato.LOGGER.warn("Invalid complexity specification: " + complexityString);
                continue;
            }

            String foodString = s[0];
            double complexity = 1.0;
            try {
                complexity = Double.parseDouble(s[1]);
            }
            catch (NumberFormatException e) {
                SOLPotato.LOGGER.warn("Second argument in complexity specification needs to be a number: " + complexityString);
                continue;
            }

            Item item;
            try {
                item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(foodString));
            }
            catch (ResourceLocationException e) {
                SOLPotato.LOGGER.warn("Invalid item name: " + foodString);
                continue;
            }
            if (item == null) {
                SOLPotato.LOGGER.warn("Invalid item name: " + foodString);
                continue;
            }

            if (!item.isFood()) {
                SOLPotato.LOGGER.warn("Item is not food: " + foodString);
                continue;
            }

            FoodInstance food = new FoodInstance(item);
            complexityMap.put(food, complexity);
        }
        return complexityMap;
    }
}
