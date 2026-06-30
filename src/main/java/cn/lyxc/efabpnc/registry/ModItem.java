package cn.lyxc.efabpnc.registry;

import cn.lyxc.efabpnc.EFabPnC;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItem {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EFabPnC.MODID);

    public static final DeferredItem<BlockItem> PNEUMATIC_INTERFACE_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlock.PNEUMATIC_INTERFACE);

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
    }
}
