package com.kevun1.solpotato.client;

import com.kevun1.solpotato.SOLPotato;
import com.kevun1.solpotato.client.gui.FoodBookScreen;
import com.kevun1.solpotato.lib.Localization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

import static com.kevun1.solpotato.client.SOLClientRegistry.OPEN_FOOD_BOOK;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SOLPotato.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {
    @SubscribeEvent
    public static void handleKeypress(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        if (OPEN_FOOD_BOOK.isPressed()) {
            FoodBookScreen.open(player);
        }
    }
}
