package com.kevun1.solpotato;

import com.kevun1.solpotato.client.SOLClientRegistry;
import com.kevun1.solpotato.communication.ConfigMessage;
import com.kevun1.solpotato.communication.FoodListMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod(SOLPotato.MOD_ID)
@Mod.EventBusSubscriber(modid = SOLPotato.MOD_ID, bus = MOD)
public final class SOLPotato {
	public static final String MOD_ID = "solpotato";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
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
			.consumer(FoodListMessage::handle)
			.add();

		channel.messageBuilder(ConfigMessage.class, 1)
				.encoder(ConfigMessage::write)
				.decoder(ConfigMessage::new)
				.consumer(ConfigMessage::handle)
				.add();
	}

	@SubscribeEvent
	public static void setupClient(FMLClientSetupEvent event) {
		SOLClientRegistry.setup();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SOLClientRegistry::registerKeybinds);
	}

	public SOLPotato() {
		SOLPotatoConfig.setUp();
	}
}
