package cn.lyxc.efabpnc.mixins;

import cn.lyxc.efabpnc.integration.kubejs.PnCRequirementJS;
import mcjty.efab.compat.kubejs.EFabGridShapedRecipeJSBuilder;
import mcjty.efab.compat.kubejs.EFabGridShapelessRecipeJSBuilder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EFabGridShapedRecipeJSBuilder.class, EFabGridShapelessRecipeJSBuilder.class})
public abstract class MixinEFabGridRecipeJSBuilder implements PnCRequirementJS {
}
