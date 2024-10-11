package com.tarinoita.solsweetpotato.item;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.item.foodcontainer.FoodContainerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SOLSweetPotatoItems
{
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, SOLSweetPotato.MOD_ID);

	public static final RegistryObject<FoodBookItem> FOOD_BOOK = REGISTER.register("food_book", FoodBookItem::new);
	public static final RegistryObject<FoodContainerItem> LUNCHBOX = REGISTER.register("lunchbox", () -> new FoodContainerItem(9,"lunchbox"));
	public static final RegistryObject<FoodContainerItem> LUNCHBAG = REGISTER.register("lunchbag", () -> new FoodContainerItem(5,"lunchbag"));
	public static final RegistryObject<FoodContainerItem> GOLDEN_LUNCHBOX = REGISTER.register("golden_lunchbox", () -> new FoodContainerItem(14,"golden_lunchbox"));
}
