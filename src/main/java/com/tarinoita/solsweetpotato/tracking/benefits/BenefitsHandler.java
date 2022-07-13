package com.tarinoita.solsweetpotato.tracking.benefits;

import com.tarinoita.solsweetpotato.ConfigHandler;
import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.SOLSweetPotatoConfig;
import com.tarinoita.solsweetpotato.tracking.CapabilityHandler;
import com.tarinoita.solsweetpotato.tracking.FoodList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * All updates to food diversity benefits go through this class.
 */
@Mod.EventBusSubscriber(modid = SOLSweetPotato.MOD_ID)
public class BenefitsHandler {
    @SubscribeEvent
    public static void tickBenefits(LivingEvent.LivingUpdateEvent event) {
        if (!checkEvent(event)) {
            return;
        }

        Player player = (Player) event.getEntity();
        
        if (!player.isAlive()) {
        	return;
        }

        EffectBenefitsCapability effectBenefits = EffectBenefitsCapability.get(player);
        effectBenefits.forEach(b -> b.onTick(player));
    }

    public static void updateBenefits(Player player, double diversity) {
        if (player.getCommandSenderWorld().isClientSide) {
            return;
        }

        FoodList foodList = FoodList.get(player);
        if (foodList.getFoodsEaten() < SOLSweetPotatoConfig.minFoodsToActivate()) {
            return;
        }

        List<List<Benefit>> benefitsList = ConfigHandler.getBenefitsList();
        List<Double> thresholds = ConfigHandler.thresholds;

        EffectBenefitsCapability effectBenefits = EffectBenefitsCapability.get(player);
        effectBenefits.clear();

        for (int i = 0; i < thresholds.size(); i++) {
            double thresh = thresholds.get(i);
            if (i >= benefitsList.size()) {
                return;
            }
            if (diversity >= thresh) {
                benefitsList.get(i).forEach(b -> b.applyTo(player));
            }
            else {
                benefitsList.get(i).forEach(b -> b.removeFrom(player));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        updatePlayer(event);
        CapabilityHandler.syncFoodList(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    	Player player = event.getPlayer();
    	player.reviveCaps();
        removeAllBenefits(player);
        player.invalidateCaps();
    }

    public static void removeAllBenefits(Player player) {
        List<List<Benefit>> benefitsList = ConfigHandler.getBenefitsList();
        benefitsList.forEach(bt -> bt.forEach(b -> b.removeFrom(player)));
    }

    public static void updatePlayer(LivingEvent event) {
        if (!checkEvent(event)) {
            return;
        }

        Player player = (Player) event.getEntity();

        updatePlayer(player);
    }

    public static void updatePlayer(Player player) {
        if (player.level.isClientSide) {
            return;
        }

        FoodList foodList = FoodList.get(player);
        double diversity = foodList.foodDiversity();

        updateBenefits(player, diversity);
    }

    public static boolean checkEvent(LivingEvent event) {
        if (!(event.getEntity() instanceof Player))
            return false;

        Player player = (Player) event.getEntity();

        if (player.level.isClientSide)
            return false;

        ServerPlayer serverPlayer = (ServerPlayer) player;
        boolean isInSurvival = serverPlayer.gameMode.isSurvival();
        return !SOLSweetPotatoConfig.limitProgressionToSurvival() || isInSurvival;
    }

    public static Pair<List<BenefitInfo>, List<BenefitInfo>> getBenefitInfo(double active_threshold, int foodEaten) {
        // Can be called on client
        List<BenefitInfo> activeBenefitInfo = new ArrayList<>();
        List<BenefitInfo> inactiveBenefitInfo = new ArrayList<>();

        if (foodEaten < SOLSweetPotatoConfig.minFoodsToActivate()) {
            active_threshold = -1;
        }

        List<List<Benefit>> benefitsList = ConfigHandler.getBenefitsList();
        List<Double> thresholds = ConfigHandler.thresholds;

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

        return new ImmutablePair<>(activeBenefitInfo, inactiveBenefitInfo);
    }
}
