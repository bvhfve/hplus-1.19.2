package net.flytre.hplus.mixin;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.HplusInit;
import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies filter upgrade to manually inserted items
 */
@Mixin(Slot.class)
public class SlotMixin {

    @Shadow
    @Final
    public Inventory inventory;

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    public void flytre_lib$filterTest(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (inventory instanceof UpgradeInventory) {
            cir.setReturnValue(MixinHelper.passFilterTest(inventory, stack));
        }
    }
}
