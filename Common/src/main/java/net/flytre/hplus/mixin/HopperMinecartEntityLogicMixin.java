package net.flytre.hplus.mixin;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.Registry;
import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Upgrade logic for the hopper minecart
 */
@Mixin(HopperMinecartEntity.class)
public abstract class HopperMinecartEntityLogicMixin extends StorageMinecartEntity implements UpgradeInventory {

    @Mutable
    @Shadow
    @Final
    private BlockPos currentBlockPos;

    protected HopperMinecartEntityLogicMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract int size();

    @Inject(method = "tick", at = @At("HEAD"))
    private void hplus$voidUpgrade(CallbackInfo ci) {
        if (!isEmpty() && MixinHelper.hasUpgrade(this, Registry.VOID_UPGRADE.get()))
            ((StorageMinecartEntityAccessor) this).flytre_lib$getInvStackList().clear();
    }

    @Inject(method = "canOperate", at = @At(value = "HEAD"), cancellable = true)
    private void hplus$cancelExtractIfLocked(CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.hasUpgrade(this, Registry.LOCK_UPGRADE.get()))
            cir.setReturnValue(false);
    }

    @Inject(method = "canOperate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntitiesByClass(Ljava/lang/Class;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"), cancellable = true)
    private void hplus$cancelItemSuckingIfRepelled(CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.hasUpgrade(this, Registry.REPELLER_UPGRADE.get()))
            cir.setReturnValue(false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void hplus$fixMinecartSpeed(CallbackInfo ci) {
        currentBlockPos = getBlockPos();
    }


    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return !MixinHelper.hasUpgrade(this, Registry.LOCK_UPGRADE.get()) && MixinHelper.passFilterTest(this, stack);
    }
}
