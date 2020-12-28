package com.kevun1.solpotato.tracking.benefits;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.UUID;

/**
 * Each instance represents a specific attribute and modifier. Handles logic for application to player.
 */
public final class AttributeBenefit extends Benefit{
    private final AttributeModifier modifier;
    private final UUID id;
    private Attribute attribute;
    private final boolean isMaxHealth;

    public AttributeBenefit(String name, double value) {
        super("attribute", name, value);

        id = UUID.randomUUID();

        modifier = new AttributeModifier(id, name + "_modifier_" + value, value, AttributeModifier.Operation.ADDITION);

        isMaxHealth = name.equals("generic.max_health");
    }

    public void applyTo(PlayerEntity player) {
        if (!checkUsage() || player.world.isRemote)
            return;

        float oldMax = player.getMaxHealth();

        ModifiableAttributeInstance attr = Objects.requireNonNull(player.getAttribute(attribute));
        attr.removeModifier(modifier);
        attr.applyPersistentModifier(modifier);

        // update health immediately
        if (isMaxHealth) {
            float newHealth = player.getHealth() * player.getMaxHealth() / oldMax;
            player.setHealth(1.0f);
            player.setHealth(newHealth);
        }
    }

    public void removeFrom(PlayerEntity player) {
        if (!checkUsage() || player.world.isRemote)
            return;

        ModifiableAttributeInstance attr = Objects.requireNonNull(player.getAttribute(attribute));
        attr.removeModifier(modifier);
    }

    private boolean checkUsage() {
        if (invalid){
            return false;
        }

        if (attribute == null) {
            createAttribute();
            return !invalid;
        }

        return true;
    }

    private void createAttribute() {
        IForgeRegistry<Attribute> registry = ForgeRegistries.ATTRIBUTES;
        try {
            attribute = registry.getValue(new ResourceLocation(name));
        }
        catch (ResourceLocationException e) {
            markInvalid();
            return;
        }

        if (attribute == null || value == 0) {
            markInvalid();
        }
    }
}
