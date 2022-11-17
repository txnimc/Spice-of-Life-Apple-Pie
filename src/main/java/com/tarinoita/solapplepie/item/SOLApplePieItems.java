package com.tarinoita.solapplepie.item;

import com.tarinoita.solapplepie.SOLApplePie;
import com.tarinoita.solapplepie.item.foodcontainer.FoodContainerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod.EventBusSubscriber(modid = SOLApplePie.MOD_ID, bus = MOD)
public final class SOLApplePieItems {

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
		entry.setRegistryName(SOLApplePie.resourceLocation(name));
		registry.register(entry);
	}
}
