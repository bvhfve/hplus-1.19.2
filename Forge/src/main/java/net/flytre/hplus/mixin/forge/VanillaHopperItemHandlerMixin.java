package net.flytre.hplus.mixin.forge;


import net.flytre.hplus.Registry;
import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.VanillaHopperItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(VanillaHopperItemHandler.class)
public class VanillaHopperItemHandlerMixin {

    @Shadow @Final private HopperBlockEntity hopper;

    @Inject(method = "insertItem", at = @At("HEAD"), cancellable = true)
    public void hplus$cancelInsertIfLocked(int slot, @Nonnull ItemStack stack, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        if(MixinHelper.hasUpgrade(hopper, Registry.LOCK_UPGRADE)) {
            cir.setReturnValue(stack);
        }
    }
}
