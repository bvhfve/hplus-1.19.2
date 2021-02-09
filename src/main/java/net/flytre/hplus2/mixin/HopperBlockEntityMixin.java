package net.flytre.hplus2.mixin;

import net.flytre.flytre_lib.common.inventory.FilterInventory;
import net.flytre.hplus2.RegistryHandler;
import net.flytre.hplus2.filter.FilterUpgrade;
import net.flytre.hplus2.filter.HopperUpgrade;
import net.flytre.hplus2.hopper.HopperPlus;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends LootableContainerBlockEntity implements Hopper, Tickable, HopperPlus, SidedInventory {


    @Shadow
    private int transferCooldown;
    @Unique
    private DefaultedList<ItemStack> upgrades;

    @Unique
    private int maxCooldown = 8;

    @Unique
    private boolean vacuum = true;

    @Unique
    private boolean trash = false;

    @Unique
    private boolean locked = false;

    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;)Z", at = @At("HEAD"), cancellable = true)
    private static void hplus$cancelExtractIfLocked(Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        if (hopper instanceof HopperPlus) {
            if (((HopperPlus) hopper).hasLockedUpgrade())
                cir.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "extract(Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isInventoryEmpty(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/util/math/Direction;)Z"))
    private static Direction hplus$extractDirection(Direction direction, Hopper hopper) {
        if (hopper instanceof HopperBlockEntity) {
            HopperBlockEntity blockEntity = (HopperBlockEntity) hopper;
            return blockEntity.getCachedState().get(HopperBlock.FACING) == Direction.UP ? Direction.UP : Direction.DOWN;
        }
        return direction;
    }

    @ModifyConstant(method = "getInputInventory", constant = @Constant(doubleValue = 1.0))
    private static double hplus$getInputInventory(double dbl, Hopper hopper) {
        double yDiff = 1.0;
        if (hopper instanceof HopperBlockEntity) {
            yDiff = ((HopperBlockEntity) hopper).getCachedState().get(HopperBlock.FACING) == Direction.UP ? -1.0 : 1.0;
        }
        return yDiff;
    }

    @ModifyConstant(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", constant = @Constant(intValue = 8))
    private static int hplus$transferCooldown(int i, @Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction direction) {
        HopperPlus dest = (HopperPlus) to;
        return dest.getMaxCooldown();
    }

    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private static void hplus$extractPassFilterTest(Hopper hopper, Inventory inventory, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = inventory.getStack(slot);
        if (hopper instanceof HopperPlus)
            if (!((HopperPlus) hopper).passFilterTest(stack))
                cir.setReturnValue(false);
    }

    @Inject(method = "extract(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/entity/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void hplus$extractItemPassFilterTest(Inventory inventory, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = itemEntity.getStack();
        if (inventory instanceof HopperPlus)
            if (!((HopperPlus) inventory).passFilterTest(stack))
                cir.setReturnValue(false);
    }

    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputItemEntities(Lnet/minecraft/block/entity/Hopper;)Ljava/util/List;"), cancellable = true)
    private static void hplus$cancelItemExtractionIfLocked(Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        if (hopper instanceof HopperPlus && !((HopperPlus) hopper).hasVacuumUpgrade())
            cir.setReturnValue(false);
    }

    @Inject(method = "insertAndExtract", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isEmpty()Z"))
    public void hplus$trashUpgrade(Supplier<Boolean> extractMethod, CallbackInfoReturnable<Boolean> cir) {
        if (!isEmpty() && trash)
            getInvStackList().clear();
    }

    @ModifyConstant(method = "insertAndExtract", constant = @Constant(intValue = 8))
    private int hplus$insertAndExtractCooldown(int i) {
        return maxCooldown;
    }

    @Inject(method = "onEntityCollided", at = @At("HEAD"), cancellable = true)
    public void hplus$cancelCollisionVacuumLocked(Entity entity, CallbackInfo ci) {
        if (!vacuum || locked)
            ci.cancel();
    }

    @Inject(method = "insertAndExtract", at = @At("HEAD"), cancellable = true)
    public void hplus$cancelInsertExtractionIfLocked(Supplier<Boolean> extractMethod, CallbackInfoReturnable<Boolean> cir) {
        if (locked)
            cir.setReturnValue(false);
    }

    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    public void hplus$cancelInsertionIfLocked(CallbackInfoReturnable<Boolean> cir) {
        if (locked)
            cir.setReturnValue(false);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void hplus$constructor(CallbackInfo ci) {
        this.upgrades = DefaultedList.ofSize(9, ItemStack.EMPTY);
    }

    @Inject(method = "toTag", at = @At("HEAD"))
    public void hplus$toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag temp = new CompoundTag();
        Inventories.toTag(temp, this.upgrades);
        Tag upgrades = temp.get("Items");
        tag.put("Upgrades", upgrades);
    }

    @Inject(method = "fromTag", at = @At("TAIL"))
    public void hplus$fromTag(BlockState state, CompoundTag tag, CallbackInfo ci) {
        Tag upgrades = tag.get("Upgrades");
        CompoundTag temp = new CompoundTag();
        temp.put("Items", upgrades);
        Inventories.fromTag(temp, this.upgrades);
        onUpgradesUpdated();
    }

    @Override
    public void onUpgradesUpdated() {
        this.vacuum = hasUpgrade(new ItemStack(RegistryHandler.VACUUM_UPGRADE));
        this.trash = hasUpgrade(new ItemStack(RegistryHandler.VOID_UPGRADE));
        this.maxCooldown = 8;
        if (hasUpgrade(new ItemStack(RegistryHandler.SPEED_UPGRADE)))
            this.maxCooldown = 4;
        if (hasUpgrade(new ItemStack(RegistryHandler.SPEED_UPGRADE_HIGH)))
            this.maxCooldown = 2;
        this.locked = hasUpgrade(new ItemStack(RegistryHandler.LOCK_UPGRADE));
    }

    @Override
    public boolean hasUpgrade(ItemStack upgrade) {

        if (!(upgrade.getItem() instanceof HopperUpgrade))
            return false;

        for (ItemStack stack : upgrades) {
            if (stack.getItem() == upgrade.getItem())
                return true;
        }
        return false;
    }

    @Override
    public boolean passFilterTest(ItemStack stack) {
        ItemStack f = null;
        for (ItemStack i : getUpgrades())
            if (i.getItem() instanceof FilterUpgrade)
                f = i;
        if (f == null)
            return true;
        FilterInventory inv = FilterUpgrade.getInventory(f);
        return inv.passFilterTest(stack);
    }

    public DefaultedList<ItemStack> getUpgrades() {
        return this.upgrades;
    }

    @Override
    public int getFirstEmptyUpgradeSlot() {
        for (int i = 0; i < getUpgrades().size(); i++)
            if (getUpgrades().get(i) == ItemStack.EMPTY)
                return i;
        return -1;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot > 4 || locked)
            return false;
        return passFilterTest(stack);
    }

    @Override
    public int getMaxCooldown() {
        return maxCooldown;
    }

    @Override
    public boolean hasVacuumUpgrade() {
        return vacuum;
    }

    @Override
    public boolean hasTrashUpgrade() {
        return trash;
    }

    @Override
    public boolean hasLockedUpgrade() {
        return locked;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1, 2, 3, 4};
    }


    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return !locked;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !locked;
    }

    /*
    Overrides default isDisabled() method
     */
    private boolean isDisabled() {
        return this.transferCooldown > maxCooldown;
    }

    /*
    Override default getStack() method
     */
    public ItemStack getStack(int slot) {

        if (slot > 4)
            return this.getUpgrades().get(slot - 5);
        return this.getInvStackList().get(slot);
    }


    /*
    Overrides default removeStack() method
     */
    public ItemStack removeStack(int slot, int amount) {

        return slot < 5 ? Inventories.splitStack(this.getInvStackList(), slot, amount) : Inventories.splitStack(this.getUpgrades(), slot - 5, amount);
    }

    /*
    Overrides default removeStack() method
    */
    public ItemStack removeStack(int slot) {
        return slot < 5 ?
                Inventories.removeStack(this.getInvStackList(), slot) :
                Inventories.removeStack(this.getUpgrades(), slot);
    }


    /*
    Overrides default setStack() method
     */
    public void setStack(int slot, ItemStack stack) {
        if (slot > 4) {
            this.getUpgrades().set(slot - 5, stack);
            return;
        }

        if (this.trash) {
            return;
        }

        this.getInvStackList().set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

    }

}
