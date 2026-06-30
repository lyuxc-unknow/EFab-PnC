package cn.lyxc.efabpnc.requirement;

import cn.lyxc.efabpnc.blockentity.PneumaticInterfaceBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.efab.api.recipe.*;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record PneumaticHeatRequirement(
        EFabRequirementPhase phase,
        double heat,
        double minTemperature,
        boolean consume
) implements EFabRecipeRequirement {

    private static final Codec<EFabRequirementPhase> PHASE_CODEC = Codec.STRING.xmap(
            name -> EFabRequirementPhase.valueOf(name.toUpperCase(java.util.Locale.ROOT)),
            phase -> phase.name().toLowerCase(java.util.Locale.ROOT)
    );

    public static final MapCodec<PneumaticHeatRequirement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PHASE_CODEC.optionalFieldOf("phase", EFabRequirementPhase.TICK).forGetter(PneumaticHeatRequirement::phase),
            Codec.DOUBLE.optionalFieldOf("heat", 0.0D).forGetter(PneumaticHeatRequirement::heat),
            Codec.DOUBLE.optionalFieldOf("min_temperature", 0.0D).forGetter(PneumaticHeatRequirement::minTemperature),
            Codec.BOOL.optionalFieldOf("consume", true).forGetter(PneumaticHeatRequirement::consume)
    ).apply(instance, PneumaticHeatRequirement::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PneumaticHeatRequirement> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull PneumaticHeatRequirement decode(RegistryFriendlyByteBuf buf) {
            return new PneumaticHeatRequirement(buf.readEnum(EFabRequirementPhase.class), buf.readDouble(), buf.readDouble(), buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PneumaticHeatRequirement value) {
            buf.writeEnum(value.phase());
            buf.writeDouble(value.heat());
            buf.writeDouble(value.minTemperature());
            buf.writeBoolean(value.consume());
        }
    };

    public static final EFabRecipeRequirementType<PneumaticHeatRequirement> TYPE =
            EFabRecipeRequirements.type(CODEC, STREAM_CODEC);

    public PneumaticHeatRequirement {
        if (heat < 0.0D) {
            throw new IllegalArgumentException("Pneumatic heat requirement must not be negative");
        }
        if (minTemperature < 0.0D) {
            throw new IllegalArgumentException("Pneumatic minimum temperature must not be negative");
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

        double required = heat * Math.max(1, amount);
        if (required <= 0.0D) {
            return interfaces.stream().map(pneumaticInterface -> pneumaticInterface.getHeatExchanger(null)).anyMatch(this::hasTemperature);
        }

        double available = 0.0D;
        for (PneumaticInterfaceBlockEntity pneumaticInterface : interfaces) {
            available += drainableHeat(pneumaticInterface.getHeatExchanger(null));
            if (available + 0.000001D >= required) {
                break;
            }
        }
        if (available + 0.000001D < required) {
            return false;
        }

        if (!simulate && consume) {
            drainHeat(interfaces, required);
        }
        return true;
    }

    @Override
    public Component description() {
        return Component.translatable("requirement.efab.pneumatic_heat", heat, minTemperature);
    }

    private boolean hasTemperature(IHeatExchangerLogic exchanger) {
        return minTemperature <= 0.0D || exchanger.getTemperature() >= minTemperature;
    }

    private double drainableHeat(IHeatExchangerLogic exchanger) {
        if (!hasTemperature(exchanger)) {
            return 0.0D;
        }
        double capacity = exchanger.getThermalCapacity();
        if (capacity <= 0.0D) {
            return 0.0D;
        }
        if (minTemperature <= 0.0D) {
            return exchanger.getTemperature() * capacity;
        }
        return Math.max(0.0D, (exchanger.getTemperature() - minTemperature) * capacity);
    }

    private void drainHeat(List<PneumaticInterfaceBlockEntity> interfaces, double required) {
        double remaining = required;
        for (PneumaticInterfaceBlockEntity pneumaticInterface : interfaces) {
            if (remaining <= 0.000001D) {
                return;
            }
            IHeatExchangerLogic exchanger = pneumaticInterface.getHeatExchanger(null);
            double drainable = drainableHeat(exchanger);
            if (drainable <= 0.0D) {
                continue;
            }
            double drained = Math.min(remaining, drainable);
            exchanger.addHeat(-drained);
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
