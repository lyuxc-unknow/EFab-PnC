package cn.lyxc.efabpnc.integration.jade;

import cn.lyxc.efabpnc.blockentity.PneumaticInterfaceBlockEntity;
import mcjty.efab.block.HorizontalEFabEntityBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeCompatibility implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(EFabPnCServerDataProvider.INSTANCE, PneumaticInterfaceBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(EFabPnCBlockComponentProvider.INSTANCE, HorizontalEFabEntityBlock.class);
    }
}
