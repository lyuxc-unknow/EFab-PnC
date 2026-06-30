package cn.lyxc.efabpnc.integration.jade;

import cn.lyxc.efabpnc.EFabPnC;
import cn.lyxc.efabpnc.blockentity.PneumaticInterfaceBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class EFabPnCServerDataProvider implements IServerDataProvider<BlockAccessor> {
    static final EFabPnCServerDataProvider INSTANCE = new EFabPnCServerDataProvider();

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        BlockEntity blockEntity = accessor.getBlockEntity();

        if (blockEntity instanceof PneumaticInterfaceBlockEntity entity) {
            tag.putFloat("efabpnc:pressure", Math.round(entity.getAirHandler(accessor.getSide()).getPressure() * 100.0) / 100.0f);
            tag.putFloat("efabpnc:danger_pressure", Math.round(entity.getAirHandler(accessor.getSide()).getDangerPressure() * 100.0) / 100.0f);
            tag.putInt("efabpnc:temperature", entity.getHeatExchanger(accessor.getSide()).getTemperatureAsInt() - 273);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return EFabPnC.rl("server_data");
    }
}
