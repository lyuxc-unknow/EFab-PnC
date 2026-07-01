package cn.lyxc.efabpnc;

import cn.lyxc.efabpnc.blockentity.PneumaticInterfaceBlockEntity;
import cn.lyxc.efabpnc.integration.theoneprobe.TOPCompatibility;
import cn.lyxc.efabpnc.registry.ModBlock;
import cn.lyxc.efabpnc.registry.ModBlockEntity;
import cn.lyxc.efabpnc.registry.ModItem;
import cn.lyxc.efabpnc.registry.ModRequirement;
import mcjty.efab.registry.ModCreativeTabs;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(EFabPnC.MODID)
public class EFabPnC {
    public static final String MODID = "efabpnc";

    public EFabPnC(IEventBus bus) {
        ModBlock.init(bus);
        ModItem.init(bus);
        ModBlockEntity.init(bus);
        ModRequirement.init();

        bus.addListener(this::registerCapabilities);
        bus.addListener(this::addItemToCreativeTab);
        bus.addListener(TOPCompatibility::enqueueIMC);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void addItemToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeTabs.MAIN.get()) {
            event.accept(ModItem.PNEUMATIC_INTERFACE_ITEM.get());
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(PNCCapabilities.AIR_HANDLER_MACHINE, ModBlockEntity.PNEUMATIC_INTERFACE_BE.get(),
                PneumaticInterfaceBlockEntity::getAirHandler);
        event.registerBlockEntity(PNCCapabilities.HEAT_EXCHANGER_BLOCK, ModBlockEntity.PNEUMATIC_INTERFACE_BE.get(),
                PneumaticInterfaceBlockEntity::getHeatExchanger);
    }
}
