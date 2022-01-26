package net.flytre.hplus.mixin.forge;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.flytre_lib.mixin.storage.upgrade.ScreenHandlerMixin;
import net.flytre.hplus.Registry;
import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
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

    @Inject(method = "ejectItems", at = @At("HEAD"), cancellable = true)
    private static void hplus$cancelInsertionIfLocked(World world, BlockPos pos, BlockState state, HopperBlockEntity inventory, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.hasUpgrade(inventory, Registry.LOCK_UPGRADE))
            cir.setReturnValue(false);
    }
}
