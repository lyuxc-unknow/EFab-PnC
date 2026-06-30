package cn.lyxc.efabpnc.registry;

import cn.lyxc.efabpnc.EFabPnC;
import cn.lyxc.efabpnc.blockentity.PneumaticInterfaceBlockEntity;
import mcjty.efab.block.NonFullHorizontalEFabEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlock {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EFabPnC.MODID);

    public static final DeferredBlock<Block> PNEUMATIC_INTERFACE = BLOCKS.register("pneumatic_interface",
            () -> new NonFullHorizontalEFabEntityBlock(machineProperties(), PneumaticInterfaceBlockEntity::new));

    private static BlockBehaviour.Properties machineProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 6.0F)
                .noOcclusion();
    }

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
