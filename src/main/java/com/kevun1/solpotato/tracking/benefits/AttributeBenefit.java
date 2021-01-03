package com.kevun1.solpotato.tracking.benefits;

import com.kevun1.solpotato.ConfigHandler;
import com.kevun1.solpotato.SOLPotato;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import sun.security.krb5.Config;

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

    public AttributeBenefit(String name, double value, String uuid) {
        super("attribute", name, value);

        id = UUID.fromString(uuid);

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

        if (isMaxHealth && !ConfigHandler.isFirstAid) {
            // increase current health proportionally
            float newHealth = player.getHealth() * player.getMaxHealth() / oldMax;
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

    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();

        StringNBT type = StringNBT.valueOf(benefitType);
        StringNBT n = StringNBT.valueOf(name);
        DoubleNBT v = DoubleNBT.valueOf(value);
        StringNBT uuid = StringNBT.valueOf(id.toString());

        tag.put("type", type);
        tag.put("name", n);
        tag.put("value", v);
        tag.put("id", uuid);

        return tag;
    }

    public static AttributeBenefit fromNBT(CompoundNBT tag) {
        String type = tag.getString("type");
        if (!type.equals("attribute")) {
            throw new RuntimeException("Mismatching benefit type");
        }
        String n = tag.getString("name");
        double v = tag.getDouble("value");
        String uuid = tag.getString("id");

        return new AttributeBenefit(n, v, uuid);
    }
}
