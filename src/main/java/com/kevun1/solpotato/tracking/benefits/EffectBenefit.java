package com.kevun1.solpotato.tracking.benefits;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
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

    public EffectBenefit(String name, double value, double threshold) {
        super("effect", name, value, threshold);
    }

    public void applyTo(PlayerEntity player) {
        if (!checkUsage() || player.world.isRemote)
            return;

        BenefitsHandler.effectBenefits.removeIf(b -> b.getName().equals(name));
        BenefitsHandler.effectBenefits.add(this);
    }

    public void onTick(PlayerEntity player) {
        if (!checkUsage() || player.world.isRemote)
            return;

        // Only refresh this effect when less than REAPPLY_DURATION ticks remaining
        EffectInstance currentEffect = player.getActivePotionEffect(effect);
        if (currentEffect != null && currentEffect.getAmplifier() >= (int) value
                && currentEffect.getDuration() > REAPPLY_DURATION) {
            return;
        }

        player.addPotionEffect(new EffectInstance(effect, DEFAULT_DURATION, (int) value, false, false));
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
        DoubleNBT thresh = DoubleNBT.valueOf(threshold);

        tag.put("type", type);
        tag.put("name", n);
        tag.put("value", v);
        tag.put("threshold", thresh);

        return tag;
    }

    public static EffectBenefit fromNBT(CompoundNBT tag) {
        String type = tag.getString("type");
        if (!type.equals("effect")) {
            throw new RuntimeException("Mismatching benefit type");
        }
        String n = tag.getString("name");
        double v = tag.getDouble("value");
        double thresh = tag.getDouble("threshold");

        return new EffectBenefit(n, v, thresh);
    }
}
