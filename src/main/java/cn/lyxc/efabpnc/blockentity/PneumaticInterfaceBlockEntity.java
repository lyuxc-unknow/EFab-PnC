package cn.lyxc.efabpnc.blockentity;

import cn.lyxc.efabpnc.registry.ModBlockEntity;
import mcjty.efab.blockentity.ServerTickingBlockEntity;
import me.desht.pneumaticcraft.api.PneumaticRegistry;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import me.desht.pneumaticcraft.api.pressure.PressureTier;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PneumaticInterfaceBlockEntity extends BlockEntity implements ServerTickingBlockEntity {

    private static final int BASE_VOLUME = 25000;
    private static final double THERMAL_CAPACITY = 500.0D;
    private static final double THERMAL_RESISTANCE = 10.0D;

    private final IAirHandlerMachine airHandler;
    private final IHeatExchangerLogic heatExchanger;
    private boolean heatInitialized;
    private int lastAir;
    private double lastTemperature;

    public PneumaticInterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.PNEUMATIC_INTERFACE_BE.get(), pos, state);
        this.airHandler = PneumaticRegistry.getInstance()
                .getAirHandlerMachineFactory()
                .createAirHandler(PressureTier.TIER_TWO, BASE_VOLUME);
        this.airHandler.setConnectableFaces(Arrays.asList(Direction.values()));
        this.heatExchanger = PneumaticRegistry.getInstance()
                .getHeatRegistry()
                .makeHeatExchangerLogic();
        this.heatExchanger.setThermalCapacity(THERMAL_CAPACITY);
        this.heatExchanger.setThermalResistance(THERMAL_RESISTANCE);
        this.lastAir = airHandler.getAir();
        this.lastTemperature = heatExchanger.getTemperature();
    }

    public IAirHandlerMachine getAirHandler(Direction direction) {
        return airHandler;
    }

    public IHeatExchangerLogic getHeatExchanger(Direction direction) {
        return heatExchanger;
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        initializeHeat();
        airHandler.tick(this);
        heatExchanger.tick();
        if (lastAir != airHandler.getAir() || Double.compare(lastTemperature, heatExchanger.getTemperature()) != 0) {
            markResourcesChanged();
        }
    }

    public void markResourcesChanged() {
        lastAir = airHandler.getAir();
        lastTemperature = heatExchanger.getTemperature();
        setChanged();
    }

    private void initializeHeat() {
        if (heatInitialized || level == null) {
            return;
        }
        heatExchanger.initializeAsHull(level, worldPosition, IHeatExchangerLogic.ALL_BLOCKS, Direction.values());
        heatInitialized = true;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Air", airHandler.serializeNBT());
        tag.put("Heat", heatExchanger.serializeNBT());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Air", Tag.TAG_COMPOUND)) {
            airHandler.deserializeNBT(tag.getCompound("Air"));
        }
        if (tag.contains("Heat", Tag.TAG_COMPOUND)) {
            heatExchanger.deserializeNBT(tag.getCompound("Heat"));
        }
        heatInitialized = false;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
