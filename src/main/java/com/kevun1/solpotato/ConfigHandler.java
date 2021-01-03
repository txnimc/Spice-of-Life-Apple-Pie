package com.kevun1.solpotato;

import com.kevun1.solpotato.communication.ConfigMessage;
import com.kevun1.solpotato.tracking.FoodInstance;
import com.kevun1.solpotato.tracking.benefits.Benefit;
import com.kevun1.solpotato.tracking.benefits.BenefitList;
import com.kevun1.solpotato.utils.BenefitsParser;
import com.kevun1.solpotato.utils.ComplexityParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SOLPotato.MOD_ID)
public class ConfigHandler {
    public static Map<FoodInstance, Double> complexityMap = new HashMap<>();
    public static List<Double> thresholds = new ArrayList<>();
    public static BenefitList benefitsList = new BenefitList(new ArrayList<>());

    public final static String COMPLEXITY_MAP_KEY = "complexity_map";
    public final static String THRESHOLDS_KEY = "thresholds";
    public final static String BENEFITS_KEY = "benefits_list";
    public final static String FOOD_KEY = "food";
    public final static String COMPLEXITY_VALUE_KEY = "complexity";
    public final static String ENTRY_KEY = "entries";

    public static boolean isFirstAid = false;

    public static CompoundNBT serializeComplexityMap() {
        CompoundNBT tag = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (Map.Entry<FoodInstance, Double> entry : complexityMap.entrySet()) {
            CompoundNBT entryTag = new CompoundNBT();
            String encoded = entry.getKey().encode();
            entryTag.put(FOOD_KEY, StringNBT.valueOf(encoded));
            entryTag.put(COMPLEXITY_VALUE_KEY, DoubleNBT.valueOf(entry.getValue()));
            list.add(entryTag);
        }
        tag.put(ENTRY_KEY, list);
        return tag;
    }

    public static ListNBT serializeThresholds() {
        ListNBT tag = new ListNBT();

        for (double t : thresholds) {
            tag.add(DoubleNBT.valueOf(t));
        }

        return tag;
    }

    public static CompoundNBT serializeBenefitsList() {
        return benefitsList.serializeNBT();
    }

    public static CompoundNBT serializeConfig() {
        CompoundNBT tag = new CompoundNBT();
        tag.put(COMPLEXITY_MAP_KEY, serializeComplexityMap());
        tag.put(THRESHOLDS_KEY, serializeThresholds());
        tag.put(BENEFITS_KEY, serializeBenefitsList());
        return tag;
    }

    public static void deserializeConfig(CompoundNBT tag) {
        deserializeComplexityMap(tag.getCompound(COMPLEXITY_MAP_KEY));
        deserializeThresholds(tag.getList(THRESHOLDS_KEY, Constants.NBT.TAG_DOUBLE));
        deserializeBenefitsList(tag.getCompound(BENEFITS_KEY));
    }

    public static void deserializeBenefitsList(CompoundNBT tag) {
        benefitsList.deserializeNBT(tag);
    }

    public static void deserializeComplexityMap(CompoundNBT tag) {
        ListNBT list = tag.getList(ENTRY_KEY, Constants.NBT.TAG_COMPOUND);
        Map<FoodInstance, Double> newComplexityMap = new HashMap<>();
        for (INBT nbt : list) {
            CompoundNBT cnbt = (CompoundNBT) nbt;
            String foodString = cnbt.getString(FOOD_KEY);
            FoodInstance food = FoodInstance.decode(foodString);
            double complexity = cnbt.getDouble(COMPLEXITY_VALUE_KEY);
            newComplexityMap.put(food, complexity);
        }
        complexityMap = newComplexityMap;
    }

    public static void deserializeThresholds(ListNBT tag) {
        List<Double> newThresholds = new ArrayList<>();
        tag.stream().map(nbt -> (DoubleNBT) nbt).map(DoubleNBT::getDouble).forEach(newThresholds::add);
        thresholds = newThresholds;
    }

    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent event) {
        complexityMap = ComplexityParser.parse(SOLPotatoConfig.getComplexityUnparsed());
        thresholds = SOLPotatoConfig.getThresholds();
        List<List<Benefit>> benefits = BenefitsParser.parse(SOLPotatoConfig.getBenefitsUnparsed());
        benefitsList = new BenefitList(benefits);

        isFirstAid = ModList.get().isLoaded("firstaid");
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        syncConfig(event.getPlayer());
    }

    public static List<List<Benefit>> getBenefitsList() {
        return benefitsList.getBenefits();
    }

    public static void syncConfig(PlayerEntity player) {
        if (player.world.isRemote) return;

        ServerPlayerEntity target = (ServerPlayerEntity) player;
        SOLPotato.channel.sendTo(
                new ConfigMessage(),
                target.connection.getNetworkManager(),
                NetworkDirection.PLAY_TO_CLIENT
        );
    }
}
