package com.cazsius.solcarrot.tracking.benefits;

import com.cazsius.solcarrot.SOLCarrot;
import com.cazsius.solcarrot.SOLCarrotConfig;
import com.cazsius.solcarrot.tracking.FoodList;
import com.cazsius.solcarrot.tracking.FoodTracker;
import javafx.util.Pair;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = SOLCarrot.MOD_ID)
public class BenefitsHandler {
    public static Set<EffectBenefit> effectBenefits = new HashSet<>();

    @SubscribeEvent
    public static void tickBenefits(LivingEvent.LivingUpdateEvent event) {
        if (!checkEvent(event)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntity();

        effectBenefits.forEach(b -> b.applyTo(player));
    }

    public static void updateBenefits(PlayerEntity player, double diversity) {
        if (player.getEntityWorld().isRemote) {
            return;
        }

        List<List<Benefit>> benefitsList = SOLCarrotConfig.getBenefitsList();
        List<Double> thresholds = SOLCarrotConfig.getThresholds();

        System.out.println("DIVERSITY: " + diversity);

        for (int i = 0; i < thresholds.size(); i++) {
            double thresh = thresholds.get(i);
            if (i >= benefitsList.size()) {
                return;
            }
            if (diversity >= thresh) {
                benefitsList.get(i).forEach(b -> System.out.println("ADDING BENEFIT: " + b));
                benefitsList.get(i).forEach(b -> b.applyTo(player));
            }
            else {
                benefitsList.get(i).forEach(b -> System.out.println("REMOVING BENEFIT: " + b));
                benefitsList.get(i).forEach(b -> b.removeFrom(player));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        updatePlayer(event);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        updatePlayer(event);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        List<List<Benefit>> benefitsList = SOLCarrotConfig.getBenefitsList();
        benefitsList.forEach(bt -> bt.forEach(b -> b.removeFrom(event.getPlayer())));
    }

    public static void updatePlayer(LivingEvent event) {
        if (!checkEvent(event)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntity();

        updatePlayer(player);
    }

    public static void updatePlayer(PlayerEntity player) {
        FoodList foodList = FoodList.get(player);
        double diversity = foodList.foodDiversity();

        updateBenefits(player, diversity);
    }

    public static boolean checkEvent(LivingEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity))
            return false;

        PlayerEntity player = (PlayerEntity) event.getEntity();

        if (player.world.isRemote)
            return false;
        ServerWorld world = (ServerWorld) player.world;

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        boolean isInSurvival = serverPlayer.interactionManager.getGameType() == GameType.SURVIVAL;
        return !SOLCarrotConfig.limitProgressionToSurvival() || isInSurvival;
    }

    public static Pair<List<BenefitInfo>, List<BenefitInfo>> getBenefitInfo(double active_threshold) {
        List<BenefitInfo> activeBenefitInfo = new ArrayList<>();
        List<BenefitInfo> inactiveBenefitInfo = new ArrayList<>();

        List<List<Benefit>> benefitsList = SOLCarrotConfig.getBenefitsList();
        List<Double> thresholds = SOLCarrotConfig.getThresholds();

        for (int i = 0; i < thresholds.size(); i++) {
            double thresh = thresholds.get(i);
            if (i >= benefitsList.size()) {
                break;
            }
            if (active_threshold >= thresh) {
                benefitsList.get(i).forEach(b -> activeBenefitInfo.add(
                        new BenefitInfo(b.getType(), b.getName(), b.getValue(), thresh)));
            }
            else {
                benefitsList.get(i).forEach(b -> inactiveBenefitInfo.add(
                        new BenefitInfo(b.getType(), b.getName(), b.getValue(), thresh)));
            }
        }

        return new Pair<>(activeBenefitInfo, inactiveBenefitInfo);
    }
}
