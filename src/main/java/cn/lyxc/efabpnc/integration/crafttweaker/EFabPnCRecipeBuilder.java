package cn.lyxc.efabpnc.integration.crafttweaker;


import cn.lyxc.efabpnc.EFabPnC;
import cn.lyxc.efabpnc.requirement.PneumaticAirRequirement;
import cn.lyxc.efabpnc.requirement.PneumaticHeatRequirement;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import mcjty.efab.api.recipe.EFabRequirementPhase;
import mcjty.efab.compat.crafttweaker.EFabGridRecipeBuilder;
import net.neoforged.fml.ModList;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Locale;

@ZenRegister
@ZenCodeType.Expansion("mods.efab.GridRecipeBuilder")
public class EFabPnCRecipeBuilder {
    @ZenCodeType.Method
    public static EFabGridRecipeBuilder pneumaticAir(EFabGridRecipeBuilder builder,int air) {
        return pneumaticAir(builder, "tick", air, 0.0F, true);
    }

    @ZenCodeType.Method
    public static EFabGridRecipeBuilder pneumaticAir(EFabGridRecipeBuilder builder, int air, float minPressure) {
        return pneumaticAir(builder, "tick", air, minPressure, true);
    }

    @ZenCodeType.Method
    public static EFabGridRecipeBuilder pneumaticAir(EFabGridRecipeBuilder builder, String phase, int air, float minPressure, boolean consume) {
        requirePneumaticCraft();
        builder.addRequirements(new PneumaticAirRequirement(parsePhase(phase), air, minPressure, consume));
        return builder;
    }

    @ZenCodeType.Method
    public static EFabGridRecipeBuilder pneumaticPressure(EFabGridRecipeBuilder builder, float minPressure) {
        return pneumaticAir(builder, "tick", 0, minPressure, false);
    }

    @ZenCodeType.Method
    public static EFabGridRecipeBuilder pneumaticHeat(EFabGridRecipeBuilder builder, double heat) {
        return pneumaticHeat(builder, "tick", heat, 0.0D, true);
    }

    @ZenCodeType.Method
    public static EFabGridRecipeBuilder pneumaticHeat(EFabGridRecipeBuilder builder, double heat, double minTemperature) {
        return pneumaticHeat(builder, "tick", heat, minTemperature, true);
    }

    @ZenCodeType.Method
    public static EFabGridRecipeBuilder pneumaticHeat(EFabGridRecipeBuilder builder, String phase, double heat, double minTemperature, boolean consume) {
        requirePneumaticCraft();
        builder.addRequirements(new PneumaticHeatRequirement(parsePhase(phase), heat, minTemperature, consume));
        return builder;
    }

    @ZenCodeType.Method
    public static EFabGridRecipeBuilder pneumaticTemperature(EFabGridRecipeBuilder builder, double minTemperature) {
        return pneumaticHeat(builder, "tick", 0.0D, minTemperature, false);
    }


    private static EFabRequirementPhase parsePhase(String phase) {
        try {
            return EFabRequirementPhase.valueOf(phase.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown EFab requirement phase: " + phase, e);
        }
    }

    private static void requirePneumaticCraft() {
        if (!ModList.get().isLoaded(EFabPnC.MODID)) {
            throw new IllegalStateException("PneumaticCraft is required for EFab pneumatic recipe requirements");
        }
    }
}
