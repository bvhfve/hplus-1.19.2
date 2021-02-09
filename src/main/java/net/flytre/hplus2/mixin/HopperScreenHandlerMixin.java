package net.flytre.hplus2.mixin;

import net.flytre.hplus2.filter.HopperUpgrade;
import net.flytre.hplus2.hopper.HopperPlus;
import net.flytre.hplus2.hopper.UpgradeSlot;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperScreenHandler.class)
public abstract class HopperScreenHandlerMixin extends ScreenHandler {

    @Mutable
    @Shadow
    @Final
    private Inventory inventory;

    private HopperPlus hopperPlus;

    protected HopperScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @ModifyVariable(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/HopperScreenHandler;checkSize(Lnet/minecraft/inventory/Inventory;I)V"))
    public Inventory modifyInventory(Inventory inventory) {
        if (!(inventory instanceof HopperPlus)) {
            this.inventory = new HopperBlockEntity();
            return this.inventory;
        } else
            return inventory;
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V", at = @At("TAIL"))
    public void addSlots(int syncId, PlayerInventory playerInventory, Inventory inventory, CallbackInfo ci) {
        //filter inventory
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new UpgradeSlot((HopperBlockEntity) inventory, 5 + l, 8 + l * 18, 14));
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


    public boolean hasUpgrade(ItemStack g) {
        return ((HopperPlus) inventory).hasUpgrade(g);
    }

    @Override
    public ItemStack onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        if (slotId > 0) {
            ItemStack stack = getSlot(slotId).getStack();
            int upgradeSlot = ((HopperPlus) inventory).getFirstEmptyUpgradeSlot();
            if (stack.getItem() instanceof HopperUpgrade && actionType == SlotActionType.QUICK_MOVE) {
                if (slotId >= 5 && slotId <= 13) {
                    if (playerEntity.inventory.insertStack(stack)) {
                        getSlot(slotId).setStack(ItemStack.EMPTY);
                        ((HopperPlus) inventory).onUpgradesUpdated();
                        return ItemStack.EMPTY;
                    }
                } else if (upgradeSlot != -1 && !hasUpgrade(stack)) {
                    ItemStack stack2 = stack.copy();
                    stack2.setCount(1);
                    stack.decrement(1);
                    ((HopperPlus) inventory).getUpgrades().set(upgradeSlot, stack2);
                    getSlot(slotId).setStack(stack);
                    ((HopperPlus) inventory).onUpgradesUpdated();
                    return ItemStack.EMPTY;
                }
            }
        }
        ItemStack stack = super.onSlotClick(slotId, clickData, actionType, playerEntity);
        ((HopperPlus) inventory).onUpgradesUpdated();
        return stack;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < this.inventory.size()) {
                if (!this.insertItem(itemStack2, 14, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 5, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemStack;
    }


}
