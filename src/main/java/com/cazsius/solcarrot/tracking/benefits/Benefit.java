package com.cazsius.solcarrot.tracking.benefits;

import com.cazsius.solcarrot.SOLCarrot;
import net.minecraft.entity.player.PlayerEntity;

public abstract class Benefit {
    private final String benefitType;
    protected final String name;
    protected final double value;
    protected boolean invalid = false;

    public Benefit(String benefitType, String name, double value) {
        this.benefitType = benefitType;
        this.name = name;
        this.value = value;
    }

    public abstract void applyTo(PlayerEntity player);

    public abstract void removeFrom(PlayerEntity player);

    @Override
    public String toString() {
        return "[" + benefitType + ", " + name + ", " + value + "]";
    }

    public boolean isInvalid() {
        return invalid;
    }

    protected void markInvalid() {
        SOLCarrot.LOGGER.warn("Invalid attribute specified in config: " + name);
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
}
