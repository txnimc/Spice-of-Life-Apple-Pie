package com.cazsius.solcarrot.tracking;

import com.cazsius.solcarrot.SOLCarrot;
import com.cazsius.solcarrot.SOLCarrotConfig;
import com.cazsius.solcarrot.tracking.benefits.BenefitsHandler;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.ArrayList;
import java.util.Collection;

import static com.cazsius.solcarrot.lib.Localization.localizedComponent;
import static com.cazsius.solcarrot.lib.Localization.localizedQuantityComponent;

@Mod.EventBusSubscriber(modid = SOLCarrot.MOD_ID)
public final class FoodTracker {

	@SubscribeEvent
	public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
		if (!BenefitsHandler.checkEvent(event)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) event.getEntity();
		
		Item usedItem = event.getItem().getItem();
		if (!usedItem.isFood()) return;

		FoodList foodList = FoodList.get(player);

		foodList.addFood(usedItem);

		double diversity = foodList.foodDiversity();

//		boolean hasTriedNewFood = foodList.addFood(usedItem);
//
//		// check this before syncing, because the sync entails an hp update
//		boolean newMilestoneReached = MaxHealthHandler.updateFoodHPModifier(player);

		BenefitsHandler.updateBenefits(player, diversity);
		
		CapabilityHandler.syncFoodList(player);

//		ProgressInfo progressInfo = foodList.getProgressInfo();
		
//		if (newMilestoneReached) {
//			if (SOLCarrotConfig.shouldPlayMilestoneSounds()) {
//				// passing the player makes it not play for some reason
//				world.playSound(
//					null,
//					player.getPosition(),
//					SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS,
//					1.0F, 1.0F
//				);
//			}
//
//			if (SOLCarrotConfig.shouldSpawnMilestoneParticles()) {
//				spawnParticles(world, player, ParticleTypes.HEART, 12);
//
//				if (progressInfo.hasReachedMax()) {
//					spawnParticles(world, player, ParticleTypes.HAPPY_VILLAGER, 16);
//				}
//			}
//
//			ITextComponent heartsDescription = localizedQuantityComponent("message", "hearts", SOLCarrotConfig.getHeartsPerMilestone());
//
//			if (SOLCarrotConfig.shouldShowProgressAboveHotbar()) {
//				String messageKey = progressInfo.hasReachedMax() ? "finished.hotbar" : "milestone_achieved";
//				player.sendStatusMessage(localizedComponent("message", messageKey, heartsDescription), true);
//			} else {
//				showChatMessage(player, TextFormatting.DARK_AQUA, localizedComponent("message", "milestone_achieved", heartsDescription));
//				if (progressInfo.hasReachedMax()) {
//					showChatMessage(player, TextFormatting.GOLD, localizedComponent("message", "finished.chat"));
//				}
//			}
//		} else if (hasTriedNewFood) {
//			if (SOLCarrotConfig.shouldSpawnIntermediateParticles()) {
//				spawnParticles(world, player, ParticleTypes.END_ROD, 12);
//			}
//		}
	}
	
	private static void spawnParticles(ServerWorld world, PlayerEntity player, IParticleData type, int count) {
		// this overload sends a packet to the client
		world.spawnParticle(
			type,
			player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ(),
			count,
			0.5F, 0.5F, 0.5F,
			0.0F
		);
	}
	
	private static void showChatMessage(PlayerEntity player, TextFormatting color, ITextComponent message) {
		ITextComponent component = localizedComponent("message", "chat_wrapper", message)
			.modifyStyle(style -> style.applyFormatting(color));
		player.sendStatusMessage(component, false);
	}
	
	private FoodTracker() {}
}
