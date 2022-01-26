package net.flytre.hplus.mixin.forge;


import net.flytre.hplus.Registry;
import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(VanillaInventoryCodeHooks.class)
public class VanillaInventoryCodeHooksMixin {

    @Shadow
    private static Optional<Pair<IItemHandler, Object>> getItemHandler(World level, Hopper hopper, Direction hopperFacing) {
        throw new AssertionError("Mixin failed");
    }

    @Redirect(method = "extractHook", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/VanillaInventoryCodeHooks;getItemHandler(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/util/math/Direction;)Ljava/util/Optional;"))
    private static Optional<Pair<IItemHandler, Object>> hplus$fixInsertionDirection(World level, Hopper hopper, Direction hopperFacing) {
        return getItemHandler(level, hopper, MixinHelper.getExtractDirection(hopper).getOpposite());
    }

    //see method insertHook which contains this lambda
    /*
    return VanillaInventoryCodeHooks.getItemHandler(hopper.getWorld(), hopper, hopperFacing).map(destinationResult -> { THIS_LAMBDA });
     */
//    @Inject(method = "lambda$insertHook$2(Lnet/minecraft/block/entity/HopperBlockEntity;Lorg/apache/commons/lang3/tuple/Pair;)Ljava/lang/Boolean", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/VanillaInventoryCodeHooks;isFull(Lnet/minecraftforge/items/IItemHandler;)Z"), cancellable = true)
//    private static void hplus$cancelInsertionIfLockedHopper(HopperBlockEntity hopper, Pair<IItemHandler, Object> destinationResult, CallbackInfoReturnable<Boolean> cir) {
//        if (MixinHelper.hasUpgrade(destinationResult.getValue(), Registry.LOCK_UPGRADE))
//            cir.setReturnValue(false);
//    }
}
