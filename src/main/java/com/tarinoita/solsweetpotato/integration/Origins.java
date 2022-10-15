package com.tarinoita.solsweetpotato.integration;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;


public class Origins {
    private static Map<Player, Boolean> hasRestrictedDiet = new HashMap<>();

    public static void cacheInvalidate(Player player) {
        hasRestrictedDiet.remove(player);
    }

    public static void clearCache() {
        hasRestrictedDiet.clear();
    }

    public static boolean hasRestrictedDiet(Player player) {
        if (hasRestrictedDiet.containsKey(player)) {
            return hasRestrictedDiet.get(player);
        }

        IOriginContainer originContainer = IOriginContainer.get(player).orElse(null);
        if (originContainer == null) {
            SOLSweetPotato.LOGGER.warn("No IOriginContainer found for player " + player.getName().getContents());
            return false;
        }

        ResourceLocation vegetarian = ResourceLocation.tryParse("origins:vegetarian");
        ResourceLocation carnivore = ResourceLocation.tryParse("origins:carnivore");

        for(Origin origin : originContainer.getOrigins().values()) {
            for (ResourceLocation power : origin.getPowers()) {
                if (power.equals(vegetarian) || power.equals(carnivore)) {
                    hasRestrictedDiet.put(player, Boolean.TRUE);
                    return true;
                }
            }

        }
        hasRestrictedDiet.put(player, Boolean.FALSE);
        return false;
    }
}
