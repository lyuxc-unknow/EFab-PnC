package cn.lyxc.efabpnc.integration.theoneprobe;

import cn.lyxc.efabpnc.EFabPnC;
import cn.lyxc.efabpnc.blockentity.PneumaticInterfaceBlockEntity;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class EFabPnCProvider implements IProbeInfoProvider {

    @Override
    public ResourceLocation getID() {
        return EFabPnC.rl("efabpnc");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity blockEntity = level.getBlockEntity(iProbeHitData.getPos());
        if (blockEntity instanceof PneumaticInterfaceBlockEntity pneumaticInterfaceBlockEntity) {
            var progress = pneumaticInterfaceBlockEntity.getAirHandler(iProbeHitData.getSideHit());
            var temperature = pneumaticInterfaceBlockEntity.getHeatExchanger(iProbeHitData.getSideHit());
            iProbeInfo.text(Component.translatable("info.efabpnc.pressure", Math.round(progress.getPressure() * 100.0) / 100.0,Math.round(progress.getDangerPressure() * 100.0) / 100.0));
            iProbeInfo.text(Component.translatable("info.efabpnc.temperature", temperature.getTemperatureAsInt() - 273));
        }
    }
}
