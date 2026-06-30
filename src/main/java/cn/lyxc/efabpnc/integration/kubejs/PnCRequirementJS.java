package cn.lyxc.efabpnc.integration.kubejs;

import cn.lyxc.efabpnc.requirement.PneumaticAirRequirement;
import cn.lyxc.efabpnc.requirement.PneumaticHeatRequirement;
import mcjty.efab.api.kubejs.RecipeJSBuilder;
import mcjty.efab.api.kubejs.RequirementJS;
import mcjty.efab.api.recipe.EFabRequirementPhase;

public interface PnCRequirementJS extends RequirementJS {

    default RecipeJSBuilder pneumaticAir(int air) {
        return pneumaticAir(air, 0.0F, true);
    }

    default RecipeJSBuilder pneumaticAir(int air, float minPressure) {
        return pneumaticAir(air, minPressure, true);
    }

    default RecipeJSBuilder pneumaticAir(int air, float minPressure, boolean consume) {
        return this.addRequirement(new PneumaticAirRequirement(EFabRequirementPhase.TICK, air, minPressure, consume));
    }

    default RecipeJSBuilder pneumaticPressure(float minPressure) {
        return pneumaticAir(0, minPressure, false);
    }

    default RecipeJSBuilder pneumaticHeat(double heat) {
        return pneumaticHeat(heat, 0.0F, true);
    }

    default RecipeJSBuilder pneumaticHeat(double heat, double minTemperature) {
        return pneumaticHeat(heat, minTemperature, true);
    }

    default RecipeJSBuilder pneumaticHeat(double heat, double minTemperature, boolean consume) {
        return this.addRequirement(new PneumaticHeatRequirement(EFabRequirementPhase.TICK, heat, minTemperature, consume));
    }

    default RecipeJSBuilder pneumaticTemperature(double minTemperature) {
        return pneumaticHeat(0.0D, minTemperature, false);
    }

}
