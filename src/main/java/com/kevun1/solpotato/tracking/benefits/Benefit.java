package com.kevun1.solpotato.tracking.benefits;

import com.kevun1.solpotato.SOLPotato;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public abstract class Benefit {
    protected final String benefitType;
    protected final String name;
    protected final double value;
    protected boolean invalid = false;
    protected final double threshold;

    public Benefit(String benefitType, String name, double value, double threshold) {
        this.benefitType = benefitType;
        this.name = name;
        this.value = value;
        this.threshold = threshold;
    }

    public abstract void applyTo(PlayerEntity player);

    public abstract void removeFrom(PlayerEntity player);

    public boolean isInvalid() {
        return invalid;
    }

    protected void markInvalid() {
        SOLPotato.LOGGER.warn("Invalid attribute specified in config: " + name);
        invalid = true;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return benefitType;
    }

    public double getValue() {
        return value;
    }

    public abstract CompoundNBT serializeNBT();

    @Override
    public String toString() {
        return "[" + benefitType + ", " + name + ", " + value + "]";
    }

    @Override
    public int hashCode() {
        int r = 31 * name.hashCode() + Double.valueOf(value).hashCode();
        return 31 * r + Double.valueOf(threshold).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Benefit)) {
            return false;
        }
        Benefit b = (Benefit) other;

        return (benefitType.equals(b.benefitType) && name.equals(b.name) && value == b.value && b.threshold == threshold);
    }
}
