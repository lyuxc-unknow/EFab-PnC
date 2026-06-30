package cn.lyxc.efabpnc.integration.jade;

import cn.lyxc.efabpnc.EFabPnC;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class EFabPnCBlockComponentProvider implements IBlockComponentProvider {
    static final EFabPnCBlockComponentProvider INSTANCE = new EFabPnCBlockComponentProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        CompoundTag data = accessor.getServerData();
        if (data.contains("efabpnc:pressure") && data.contains("efabpnc:danger_pressure")) {
            tooltip.add(Component.translatable("info.efabpnc.pressure",data.getFloat("efabpnc:pressure"), data.getFloat("efabpnc:danger_pressure")));
        }

        if (data.contains("efabpnc:temperature")) {
            tooltip.add(Component.translatable("info.efabpnc.temperature", data.getFloat("efabpnc:temperature")));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return EFabPnC.rl("fabpnc_block_component");
    }
}
