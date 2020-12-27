package com.cazsius.solcarrot.tracking.benefits;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public final class EffectBenefit extends Benefit{
    private Effect effect;
    private final int DEFAULT_DURATION = 300;

    public EffectBenefit(String name, double value) {
        super("effect", name, value);
    }

    public void applyTo(PlayerEntity player) {
        if (!checkUsage() || player.world.isRemote)
            return;

        player.addPotionEffect(new EffectInstance(effect, DEFAULT_DURATION, (int) value, false, false));
        BenefitsHandler.effectBenefits.add(this);
    }

    public void removeFrom(PlayerEntity player) {
        if (!checkUsage() || player.world.isRemote)
            return;

        BenefitsHandler.effectBenefits.remove(this);
    }

    private boolean checkUsage() {
        if (invalid){
            return false;
        }

        if (effect == null) {
            createEffect();
            return !invalid;
        }

        return true;
    }

    private void createEffect() {
        IForgeRegistry<Effect> registry = RegistryManager.ACTIVE.getRegistry(ForgeRegistries.Keys.EFFECTS);
        try {
            effect = registry.getValue(new ResourceLocation(name));
        }
        catch (ResourceLocationException e) {
            markInvalid();
            return;
        }

        if (effect == null) {
            markInvalid();
        }

        if (value < 0 || value > 255) {
            markInvalid();
        }
    }
}
