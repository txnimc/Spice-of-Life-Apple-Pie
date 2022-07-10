package com.kevun1.solpotato.client;

import com.kevun1.solpotato.lib.Localization;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import com.mojang.blaze3d.platform.InputConstants;


@OnlyIn(Dist.CLIENT)
public class SOLClientRegistry {
    public static KeyMapping OPEN_FOOD_BOOK;

    public static void registerKeybinds() {
        OPEN_FOOD_BOOK = new KeyMapping(Localization.localized("key", "open_food_book"),
                InputConstants.UNKNOWN.getValue(), Localization.localized("key", "category"));
        ClientRegistry.registerKeyBinding(OPEN_FOOD_BOOK);
    }

    public static void setup() {
        registerKeybinds();
    }
}

