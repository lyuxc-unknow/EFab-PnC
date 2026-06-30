package cn.lyxc.efabpnc.requirement;

import cn.lyxc.efabpnc.blockentity.PneumaticInterfaceBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.efab.api.recipe.*;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record PneumaticAirRequirement(
        EFabRequirementPhase phase,
        int air,
        float minPressure,
        boolean consume
) implements EFabRecipeRequirement {

    private static final Codec<EFabRequirementPhase> PHASE_CODEC = Codec.STRING.xmap(
            name -> EFabRequirementPhase.valueOf(name.toUpperCase(Locale.ROOT)),
            phase -> phase.name().toLowerCase(Locale.ROOT)
    );

    public static final MapCodec<PneumaticAirRequirement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PHASE_CODEC.optionalFieldOf("phase", EFabRequirementPhase.TICK).forGetter(PneumaticAirRequirement::phase),
            Codec.INT.optionalFieldOf("air", 0).forGetter(PneumaticAirRequirement::air),
            Codec.FLOAT.optionalFieldOf("min_pressure", 0.0F).forGetter(PneumaticAirRequirement::minPressure),
            Codec.BOOL.optionalFieldOf("consume", true).forGetter(PneumaticAirRequirement::consume)
    ).apply(instance, PneumaticAirRequirement::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PneumaticAirRequirement> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull PneumaticAirRequirement decode(RegistryFriendlyByteBuf buf) {
            return new PneumaticAirRequirement(buf.readEnum(EFabRequirementPhase.class), buf.readVarInt(), buf.readFloat(), buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PneumaticAirRequirement value) {
            buf.writeEnum(value.phase());
            buf.writeVarInt(value.air());
            buf.writeFloat(value.minPressure());
            buf.writeBoolean(value.consume());
        }
    };

    public static final EFabRecipeRequirementType<PneumaticAirRequirement> TYPE =
            EFabRecipeRequirements.type(CODEC, STREAM_CODEC);

    public PneumaticAirRequirement {
        if (air < 0) {
            throw new IllegalArgumentException("Pneumatic air requirement must not be negative");
        }
        if (minPressure < 0.0F) {
            throw new IllegalArgumentException("Pneumatic minimum pressure must not be negative");
        }
    }

    @Override
    public EFabRecipeRequirementType<? extends EFabRecipeRequirement> type() {
        return TYPE;
    }

    @Override
    public boolean apply(EFabCraftingContext context, EFabRequirementPhase phase, int amount, boolean simulate) {
        if (phase != this.phase) {
            return true;
        }

        List<PneumaticInterfaceBlockEntity> interfaces = findInterfaces(context);
        if (interfaces.isEmpty()) {
            return false;
        }

        long required = (long) air * Math.max(1, amount);
        if (required <= 0L) {
            return interfaces.stream().map(pneumaticInterface -> pneumaticInterface.getAirHandler(null)).anyMatch(this::hasPressure);
        }

        long available = 0L;
        for (PneumaticInterfaceBlockEntity pneumaticInterface : interfaces) {
            available += drainableAir(pneumaticInterface.getAirHandler(null));
            if (available >= required) {
                break;
            }
        }
        if (available < required) {
            return false;
        }

        if (!simulate && consume) {
            drainAir(interfaces, required);
        }
        return true;
    }

    @Override
    public Component description() {
        return Component.translatable("requirement.efab.pneumatic_air", air, minPressure);
    }

    private boolean hasPressure(IAirHandlerMachine handler) {
        return minPressure <= 0.0F || handler.getPressure() >= minPressure;
    }

    private long drainableAir(IAirHandlerMachine handler) {
        if (!hasPressure(handler)) {
            return 0L;
        }
        int stored = handler.getAir();
        if (stored <= 0) {
            return 0L;
        }
        if (minPressure <= 0.0F) {
            return stored;
        }
        int reserve = (int) Math.ceil(minPressure * handler.getVolume());
        return Math.max(0, stored - reserve);
    }

    private void drainAir(List<PneumaticInterfaceBlockEntity> interfaces, long required) {
        long remaining = required;
        for (PneumaticInterfaceBlockEntity pneumaticInterface : interfaces) {
            if (remaining <= 0L) {
                return;
            }
            IAirHandlerMachine handler = pneumaticInterface.getAirHandler(null);
            int drainable = (int) Math.min(Integer.MAX_VALUE, drainableAir(handler));
            if (drainable <= 0) {
                continue;
            }
            int drained = (int) Math.min(remaining, drainable);
            handler.addAir(-drained);
            pneumaticInterface.markResourcesChanged();
            remaining -= drained;
        }
    }

    private static List<PneumaticInterfaceBlockEntity> findInterfaces(EFabCraftingContext context) {
        Level level = context.level();
        Set<PneumaticInterfaceBlockEntity> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        List<PneumaticInterfaceBlockEntity> interfaces = new ArrayList<>();
        for (BlockPos pos : context.craftingArea()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PneumaticInterfaceBlockEntity pneumaticInterface) {
                if (seen.add(pneumaticInterface)) {
                    interfaces.add(pneumaticInterface);
                }
            }
        }
        return interfaces;
    }
}
