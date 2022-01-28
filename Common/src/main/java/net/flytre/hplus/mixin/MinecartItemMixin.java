package net.flytre.hplus.mixin;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecartItem.class)
public class MinecartItemMixin {

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void hplus$copyUpgradesToDispensedMinecart(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir, World world, BlockPos blockPos, BlockState blockState, ItemStack stack, RailShape railShape, double yOffset, AbstractMinecartEntity minecartEntity) {
        @Nullable NbtCompound nbt = stack.getSubNbt("EntityTag");
        if (nbt != null && nbt.contains("Upgrades"))
            UpgradeInventory.fromTag(nbt, ((UpgradeInventory) minecartEntity).getUpgrades());
    }
}
