package net.flytre.hplus.mixin.forge;


import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.block.entity.Hopper;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(VanillaInventoryCodeHooks.class)
public class VanillaInventoryCodeHooksMixin {

    @Shadow
    private static Optional<Pair<IItemHandler, Object>> getItemHandler(World level, Hopper hopper, Direction hopperFacing) {
        throw new AssertionError("Mixin failed");
    }

    @Redirect(method = "extractHook", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/VanillaInventoryCodeHooks;getItemHandler(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/util/math/Direction;)Ljava/util/Optional;"))
    private static Optional<Pair<IItemHandler, Object>> hplus$fixExtractionDirection(World level, Hopper hopper, Direction hopperFacing) {
        return getItemHandler(level, hopper, MixinHelper.getExtractDirection(hopper));
    }
}
