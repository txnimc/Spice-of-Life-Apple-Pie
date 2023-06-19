package com.tarinoita.solapplepie.client;

import com.tarinoita.solapplepie.SOLApplePie;
import com.tarinoita.solapplepie.client.gui.FoodBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.tarinoita.solapplepie.client.SOLClientRegistry.OPEN_FOOD_BOOK;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SOLApplePie.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {
    @SubscribeEvent
    public static void handleKeypress(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }

        if (OPEN_FOOD_BOOK.isDown()) {
            FoodBookScreen.open(player);
        }
    }
}
