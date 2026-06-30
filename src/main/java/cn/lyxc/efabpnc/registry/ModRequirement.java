package cn.lyxc.efabpnc.registry;

import cn.lyxc.efabpnc.EFabPnC;
import cn.lyxc.efabpnc.requirement.PneumaticAirRequirement;
import cn.lyxc.efabpnc.requirement.PneumaticHeatRequirement;
import mcjty.efab.api.recipe.EFabRecipeRequirements;

public class ModRequirement {
    private static void registerRequirement() {
        if (!EFabRecipeRequirements.contains(EFabPnC.rl("pneumatic_air"))) {
            EFabRecipeRequirements.register(EFabPnC.rl("pneumatic_air"), PneumaticAirRequirement.TYPE);
        }
        if (!EFabRecipeRequirements.contains(EFabPnC.rl("pneumatic_heat"))) {
            EFabRecipeRequirements.register(EFabPnC.rl("pneumatic_heat"), PneumaticHeatRequirement.TYPE);
        }
    }

    public static void init() {
        registerRequirement();
    }
}
