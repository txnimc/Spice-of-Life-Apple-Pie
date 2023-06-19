package com.tarinoita.solsweetpotato.integration;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
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
            SOLSweetPotato.LOGGER.warn("No IOriginContainer found for player: " + player.getName().getContents());
            return false;
        }

        String vegetarian = "[origins:vegetarian]";
        String carnivore = "[origins:carnivore]";

        Registry<Origin> originRegistry = OriginsAPI.getOriginsRegistry();
        for(ResourceKey<Origin> originKey : originContainer.getOrigins().values()) {
            Origin origin = originRegistry.get(originKey);
            if (origin == null) {
                SOLSweetPotato.LOGGER.warn("Player " + player.getName().getContents() +
                        " has unregistered Origin: " + originKey.toString());
                continue;
            }
            String originAsString = origin.toString();
            if (originAsString.contains(vegetarian) || originAsString.contains(carnivore)) {
                hasRestrictedDiet.put(player, Boolean.TRUE);
                return true;
            }
        }
        hasRestrictedDiet.put(player, Boolean.FALSE);
        return false;
    }
}
