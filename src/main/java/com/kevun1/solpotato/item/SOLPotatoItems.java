package com.kevun1.solpotato.item;

import com.kevun1.solpotato.SOLPotato;
import com.kevun1.solpotato.item.foodcontainer.FoodContainerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod.EventBusSubscriber(modid = SOLPotato.MOD_ID, bus = MOD)
public final class SOLPotatoItems {

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		register(registry, new FoodBookItem(), "food_book");
		FoodContainerItem lunchbox = new FoodContainerItem(9, "lunchbox");
		register(registry, lunchbox, "lunchbox");
		FoodContainerItem lunchbag = new FoodContainerItem(5, "lunchbag");
		register(registry, lunchbag, "lunchbag");
		FoodContainerItem golden_lunchbox = new FoodContainerItem(14, "golden_lunchbox");
		register(registry, golden_lunchbox, "golden_lunchbox");
	}
	
	public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, T entry, String name) {
		entry.setRegistryName(SOLPotato.resourceLocation(name));
		registry.register(entry);
	}
}
