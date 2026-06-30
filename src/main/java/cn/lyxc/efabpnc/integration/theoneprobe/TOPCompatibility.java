package cn.lyxc.efabpnc.integration.theoneprobe;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.function.Function;

public class TOPCompatibility {
    private static final String TOP_MODID = "theoneprobe";

    public static void enqueueIMC(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded(TOP_MODID)) {
            InterModComms.sendTo(TOP_MODID, "getTheOneProbe", TOPCompatibility.GetTheOneProbe::new);
        }
    }

    public static final class GetTheOneProbe implements Function<ITheOneProbe, Void> {

        @Override
        public Void apply(ITheOneProbe probe) {
            probe.registerProvider(new EFabPnCProvider());
            return null;
        }
    }
}
