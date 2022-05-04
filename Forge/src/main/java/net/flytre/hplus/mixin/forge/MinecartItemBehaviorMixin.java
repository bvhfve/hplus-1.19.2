package net.flytre.hplus.mixin.forge;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Mixin to the anonymous ItemDispenserBehavior instance
 * Make dispensers respect upgrades on minecarts.
 */
@Mixin(targets = "net.minecraft.item.MinecartItem$1")
public class MinecartItemBehaviorMixin {

    @Inject(method = "dispenseSilently", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void hplus$copyUpgradesToDispensedMinecart(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir, Direction direction, World level, double x, double y, double z, BlockPos blockPos, BlockState blockState, RailShape railShape, double yOffset, AbstractMinecartEntity minecartEntity) {
        @Nullable NbtCompound nbt = stack.getSubNbt("EntityTag");
        if (nbt != null && nbt.contains("Upgrades"))
            UpgradeInventory.fromTag(nbt, ((UpgradeInventory) minecartEntity).getUpgrades());
    }

}
