package com.kevun1.solpotato.tracking.benefits;

import com.kevun1.solpotato.api.FoodCapability;
import com.kevun1.solpotato.api.SOLPotatoAPI;
import com.kevun1.solpotato.tracking.CapabilityHandler;
import com.kevun1.solpotato.tracking.FoodList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class EffectBenefitsCapability implements ICapabilitySerializable<CompoundNBT>, Iterable<EffectBenefit>{
    private final Set<EffectBenefit> effectBenefits = new HashSet<>();

    private final LazyOptional<EffectBenefitsCapability> capabilityOptional = LazyOptional.of(() -> this);
    private static final String NBT_KEY_EFFECT_BENEFITS = "effect_benefits";

    public static EffectBenefitsCapability get(PlayerEntity player) {
        return player.getCapability(CapabilityHandler.effectBenefitsCapability)
                .orElseThrow(EffectBenefitsCapability.EffectsBenefitsNotFoundException::new);
    }

    public void addEffectBenefit(EffectBenefit b) {
        effectBenefits.add(b);
    }

    public void addEffectBenefitUnique(EffectBenefit b) {
        effectBenefits.removeIf(other -> other.getName().equals(b.getName()));
        addEffectBenefit(b);
    }

    public void removeEffectBenefit(EffectBenefit b) {
        effectBenefits.remove(b);
    }

    public void clear() {
        effectBenefits.clear();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        return capability == CapabilityHandler.effectBenefitsCapability ? capabilityOptional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();

        ListNBT list = new ListNBT();
        effectBenefits.stream().map(EffectBenefit::serializeNBT).forEach(list::add);
        tag.put(NBT_KEY_EFFECT_BENEFITS, list);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        ListNBT list = tag.getList(NBT_KEY_EFFECT_BENEFITS, Constants.NBT.TAG_COMPOUND);

        effectBenefits.clear();
        list.stream()
                .map(nbt-> (CompoundNBT) nbt)
                .map(EffectBenefit::fromNBT)
                .forEach(effectBenefits::add);
    }

    @Nonnull
    @Override
    public Iterator<EffectBenefit> iterator() {
        return effectBenefits.iterator();
    }

    public static final class Storage implements Capability.IStorage<EffectBenefitsCapability> {
        @Override
        public INBT writeNBT(Capability<EffectBenefitsCapability> capability, EffectBenefitsCapability instance,
                             Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<EffectBenefitsCapability> capability, EffectBenefitsCapability instance,
                            Direction side, INBT tag) {
            instance.deserializeNBT((CompoundNBT) tag);
        }
    }

    public static class EffectsBenefitsNotFoundException extends RuntimeException {
        public EffectsBenefitsNotFoundException() {
            super("Player must have effect benefits capability attached, but none was found.");
        }
    }
}
