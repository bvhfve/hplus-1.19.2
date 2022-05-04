package net.flytre.hplus.mixin.fabric;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.Registry;
import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Actual logic to make the mixins work ~ In a separate file for organization
 */
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityLogicMixin extends LootableContainerBlockEntity implements Hopper, UpgradeInventory, SidedInventory {

    protected HopperBlockEntityLogicMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }


    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    private static void hplus$cancelInsertionIfLocked(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.hasUpgrade(inventory, Registry.LOCK_UPGRADE.get()))
            cir.setReturnValue(false);
    }
}
