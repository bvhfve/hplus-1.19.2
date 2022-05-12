package net.flytre.hplus.mixin.fabric;

import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = HopperBlockEntity.class, priority = 1)
public abstract class HopperBlockEntityHookOverride {

    @Unique
    private static final Inventory TEMP_INVENTORY = new SimpleInventory(3);

    //TODO: ADD API SUPPORT CLAUSE AT END
    @ModifyVariable(
            method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
            at = @At("STORE")
    )
    private static Inventory hplus$cancelHook1(Inventory inventory, World world, Hopper hopper) {
        if (MixinHelper.getExtractDirection(hopper) != Direction.UP && inventory == null)
            return TEMP_INVENTORY;
        return inventory;
    }

    @ModifyVariable(
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputInventory(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Lnet/minecraft/inventory/Inventory;",
                    shift = At.Shift.AFTER
            ),
            method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z"
    )
    private static Inventory hplus$cancelHook2(Inventory inventory) {
        if (inventory == TEMP_INVENTORY)
            return null;
        return inventory;
    }


    // Api is experimental so don't support YET
//    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At("TAIL"), cancellable = true)
//    private static void hplus$fabricApiExtractHook(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
//
//        if (MixinHelper.getExtractDirection(hopper) == Direction.DOWN)
//            return;
//
//        BlockPos sourcePos = new BlockPos(hopper.getHopperX(), hopper.getHopperY() + 1.0D, hopper.getHopperZ());
//        Storage<ItemVariant> source = ItemStorage.SIDED.find(world, sourcePos, MixinHelper.getExtractDirection(hopper));
//        if (source != null) {
//            long moved = StorageUtil.move(source, InventoryStorage.of(hopper, MixinHelper.getExtractDirection(hopper).getOpposite()), (iv) -> true, 1L, null);
//            cir.setReturnValue(moved == 1L);
//        }
//
//    }
}

