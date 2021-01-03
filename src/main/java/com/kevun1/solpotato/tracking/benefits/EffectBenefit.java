package com.kevun1.solpotato.tracking.benefits;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

/**
 * Each instance represents a specific potion effect. Handles logic for application to player.
 */
public final class EffectBenefit extends Benefit{
    private Effect effect;
    private final int DEFAULT_DURATION = 300;
    private final int REAPPLY_DURATION = 200;

    public EffectBenefit(String name, double value) {
        super("effect", name, value);
    }

    public void applyTo(PlayerEntity player) {
        if (!checkUsage() || player.world.isRemote)
            return;

        // Only refresh this effect when less than REAPPLY_DURATION ticks remaining
        EffectInstance current_effect = player.getActivePotionEffect(effect);
        if (current_effect != null && current_effect.getAmplifier() >= (int) value
                && current_effect.getDuration() > REAPPLY_DURATION) {
            return;
        }

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
            return;
        }

        if (value < 0 || value > 255) {
            markInvalid();
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();

        StringNBT type = StringNBT.valueOf(benefitType);
        StringNBT n = StringNBT.valueOf(name);
        DoubleNBT v = DoubleNBT.valueOf(value);

        tag.put("type", type);
        tag.put("name", n);
        tag.put("value", v);

        return tag;
    }

    public static EffectBenefit fromNBT(CompoundNBT tag) {
        String type = tag.getString("type");
        if (!type.equals("effect")) {
            throw new RuntimeException("Mismatching benefit type");
        }
        String n = tag.getString("name");
        double v = tag.getDouble("value");

        return new EffectBenefit(n, v);
    }
}
