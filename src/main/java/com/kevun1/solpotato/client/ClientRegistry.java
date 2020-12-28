package com.kevun1.solpotato.client;

import com.kevun1.solpotato.item.SOLPotatoItems;
import com.kevun1.solpotato.item.foodcontainer.FoodContainerScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientRegistry {

    public static void setup() {
        ScreenManager.registerFactory(ContainerScreenRegistry.food_container, FoodContainerScreen::new);
    }
}

