package com.tarinoita.solsweetpotato;

import com.tarinoita.solsweetpotato.client.ContainerScreenRegistry;
import com.tarinoita.solsweetpotato.communication.ConfigMessage;
import com.tarinoita.solsweetpotato.communication.FoodListMessage;
import com.tarinoita.solsweetpotato.item.SOLSweetPotatoItems;
import com.tarinoita.solsweetpotato.item.foodcontainer.FoodContainerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod(SOLSweetPotato.MOD_ID)
@Mod.EventBusSubscriber(modid = SOLSweetPotato.MOD_ID, bus = MOD)
public final class SOLSweetPotato
{
	public static final String MOD_ID = "solapplepie";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static boolean HasFarmersDelight() { return ModList.get().isLoaded("farmersdelight"); }
	public static boolean HasPamsHarvestcraft() { return ModList.get().isLoaded("pamhc2foodcore"); }

	private static final String PROTOCOL_VERSION = "1.0";
	public static SimpleChannel channel = NetworkRegistry.ChannelBuilder
		.named(resourceLocation("main"))
		.clientAcceptedVersions(PROTOCOL_VERSION::equals)
		.serverAcceptedVersions(PROTOCOL_VERSION::equals)
		.networkProtocolVersion(() -> PROTOCOL_VERSION)
		.simpleChannel();
	
	public static ResourceLocation resourceLocation(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	@SubscribeEvent
	public static void setUp(FMLCommonSetupEvent event) {
		channel.messageBuilder(FoodListMessage.class, 0)
			.encoder(FoodListMessage::write)
			.decoder(FoodListMessage::new)
			.consumerMainThread(FoodListMessage::handle)
			.add();

		channel.messageBuilder(ConfigMessage.class, 1)
				.encoder(ConfigMessage::write)
				.decoder(ConfigMessage::new)
				.consumerMainThread(ConfigMessage::handle)
				.add();
	}

	@SubscribeEvent
	public static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
			event.accept(SOLSweetPotatoItems.FOOD_BOOK.get());
			event.accept(SOLSweetPotatoItems.LUNCHBOX.get());
			event.accept(SOLSweetPotatoItems.LUNCHBAG.get());
			event.accept(SOLSweetPotatoItems.GOLDEN_LUNCHBOX.get());
		}
	}

	@SubscribeEvent
	public static void setupClient(FMLClientSetupEvent event) {
		event.enqueueWork(() -> { MenuScreens.register(ContainerScreenRegistry.FOOD_CONTAINER.get(), FoodContainerScreen::new); });
	}

	public SOLSweetPotato() {
		SOLSweetPotatoConfig.setUp();
		SOLSweetPotatoItems.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		ContainerScreenRegistry.MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
