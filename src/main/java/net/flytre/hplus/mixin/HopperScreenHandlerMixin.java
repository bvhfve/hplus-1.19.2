package net.flytre.hplus.mixin;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeSlot;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Add upgrade slots to the hopper screen handler
 */
@Mixin(HopperScreenHandler.class)
public abstract class HopperScreenHandlerMixin implements UpgradeHandler {

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V", at = @At("TAIL"))
    public void flytre_lib$upgradeSlots(int syncId, PlayerInventory playerInventory, Inventory inventory, CallbackInfo ci) {
        if (inventory instanceof SimpleInventory)
            inventory = new HopperBlockEntity(BlockPos.ORIGIN, Blocks.HOPPER.getDefaultState());

        //slots
        for (int l = 2; l < 7; ++l) {
            this.addSlot(new UpgradeSlot((UpgradeInventory) inventory, l - 2, 8 + l * 18, 14));
        }
    }


    @ModifyConstant(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V", constant = @Constant(intValue = 20))
    public int hplus$setSlotCoord(int i) {
        return 41;
    }

    @ModifyConstant(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V", constant = @Constant(intValue = 51))
    public int hplus$setSlotCoord2(int i) {
        return 67;
    }

    @ModifyConstant(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V", constant = @Constant(intValue = 109))
    public int hplus$setSlotCoord3(int i) {
        return 125;
    }
}
