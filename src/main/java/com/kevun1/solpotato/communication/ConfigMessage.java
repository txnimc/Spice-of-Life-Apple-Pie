package com.kevun1.solpotato.communication;

import com.kevun1.solpotato.ConfigHandler;
import com.kevun1.solpotato.tracking.benefits.BenefitList;
import com.kevun1.solpotato.tracking.benefits.BenefitsHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ConfigMessage {
    private CompoundNBT configNBT;

    public ConfigMessage() {
        this.configNBT = ConfigHandler.serializeConfig();
    }

    public ConfigMessage(PacketBuffer buffer) {
        this.configNBT = buffer.readCompoundTag();
    }

    public void write(PacketBuffer buffer) {
        buffer.writeCompoundTag(configNBT);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ConfigMessage.Handler.handle(this, context));
    }

    private static class Handler {
        static void handle(ConfigMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                ConfigHandler.deserializeConfig(message.configNBT);
            });
            context.get().setPacketHandled(true);
        }
    }
}
