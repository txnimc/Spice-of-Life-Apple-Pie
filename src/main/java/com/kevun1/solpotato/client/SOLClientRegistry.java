package com.kevun1.solpotato.client;

import com.kevun1.solpotato.item.SOLPotatoItems;
import com.kevun1.solpotato.item.foodcontainer.FoodContainerScreen;
import com.kevun1.solpotato.lib.Localization;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;


@OnlyIn(Dist.CLIENT)
public class SOLClientRegistry {
    public static KeyBinding OPEN_FOOD_BOOK;

    public static void registerKeybinds() {
        OPEN_FOOD_BOOK = new KeyBinding(Localization.localized("key", "open_food_book"),
                InputMappings.INPUT_INVALID.getKeyCode(), Localization.localized("key", "category"));
        ClientRegistry.registerKeyBinding(OPEN_FOOD_BOOK);
    }

    public static void setup() {
        ScreenManager.registerFactory(ContainerScreenRegistry.food_container, FoodContainerScreen::new);
        registerKeybinds();
    }
}

