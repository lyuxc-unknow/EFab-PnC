package cn.lyxc.efabpnc.registry;

import cn.lyxc.efabpnc.EFabPnC;
import cn.lyxc.efabpnc.blockentity.PneumaticInterfaceBlockEntity;
import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntity {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EFabPnC.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PneumaticInterfaceBlockEntity>> PNEUMATIC_INTERFACE_BE =
            BLOCK_ENTITY_TYPES.register("pneumatic_interface", () -> BlockEntityType.Builder.of(
                    PneumaticInterfaceBlockEntity::new,
                    ModBlock.PNEUMATIC_INTERFACE.get()
            ).build(DSL.emptyPartType()));

    public static void init(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}
