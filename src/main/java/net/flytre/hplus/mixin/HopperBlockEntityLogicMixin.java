package net.flytre.hplus.mixin;


import net.flytre.flytre_lib.api.storage.inventory.filter.FilterInventory;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.HplusInit;
import net.flytre.hplus.filter.FilterUpgrade;
import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

/**
 * Actual logic to make the mixins work ~ In a separate file for organization
 */
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityLogicMixin extends LootableContainerBlockEntity implements Hopper, UpgradeInventory, SidedInventory {
    @Shadow
    private int transferCooldown;


    protected HopperBlockEntityLogicMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow
    @Nullable
    private static Inventory getInputInventory(World world, Hopper hopper) {
        throw new AssertionError("Mixin failed");
    }


    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At("HEAD"), cancellable = true)
    private static void hplus$cancelExtractIfLocked(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.hasUpgrade(hopper, HplusInit.LOCK_UPGRADE))
            cir.setReturnValue(false);

        Inventory inventory = getInputInventory(world, hopper);

        if (MixinHelper.hasUpgrade(inventory, HplusInit.LOCK_UPGRADE))
            cir.setReturnValue(false);
    }

    @ModifyConstant(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", constant = @Constant(intValue = 8))
    private static int hplus$transferCooldown(int i, @Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction direction) {
        return MixinHelper.getMaxCooldown(to);
    }

    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private static void hplus$extractPassFilterTest(Hopper hopper, Inventory inventory, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = inventory.getStack(slot);
        if (!MixinHelper.passFilterTest(hopper, stack))
            cir.setReturnValue(false);
    }


    @Inject(method = "extract(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/entity/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void hplus$extractItemPassFilterTest(Inventory inventory, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = itemEntity.getStack();
        if (!MixinHelper.passFilterTest(inventory, stack))
            cir.setReturnValue(false);
    }

    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputItemEntities(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Ljava/util/List;"), cancellable = true)
    private static void hplus$cancelItemExtractionIfLocked(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.hasUpgrade(hopper, HplusInit.REPELLER_UPGRADE))
            cir.setReturnValue(false);
    }


    @Inject(method = "insertAndExtract", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isEmpty()Z"))
    private static void hplus$trashUpgrade(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> cir) {
        if (!blockEntity.isEmpty() && MixinHelper.hasUpgrade(blockEntity, HplusInit.VOID_UPGRADE))
            ((LootableContainerBlockEntityInvoker) blockEntity).flytre_lib$getInventoryContents().clear();
    }

    @ModifyConstant(method = "insertAndExtract", constant = @Constant(intValue = 8))
    private static int hplus$insertAndExtractCooldown(int eight, World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
        return MixinHelper.getMaxCooldown(blockEntity);
    }

    @Inject(method = "onEntityCollided", at = @At("HEAD"), cancellable = true)
    private static void hplus$cancelCollisionVacuumLocked(World world, BlockPos pos, BlockState state, Entity entity, HopperBlockEntity blockEntity, CallbackInfo ci) {
        if (MixinHelper.hasUpgrade(blockEntity, HplusInit.REPELLER_UPGRADE) || MixinHelper.hasUpgrade(blockEntity, HplusInit.LOCK_UPGRADE))
            ci.cancel();
    }

    @Inject(method = "insertAndExtract", at = @At("HEAD"), cancellable = true)
    private static void hplus$cancelInsertExtractionIfLocked(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.hasUpgrade(blockEntity, HplusInit.LOCK_UPGRADE))
            cir.setReturnValue(false);
    }

    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    private static void hplus$cancelInsertionIfLocked(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.hasUpgrade(inventory, HplusInit.LOCK_UPGRADE))
            cir.setReturnValue(false);
    }


    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return !MixinHelper.hasUpgrade(this, HplusInit.LOCK_UPGRADE);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !MixinHelper.hasUpgrade(this, HplusInit.LOCK_UPGRADE);
    }


    @Override
    public int[] getAvailableSlots(Direction side) {
        return IntStream.range(0, 5).toArray();
    }

    /**
     * Overrides default isDisabled() method
     */
    @SuppressWarnings("unused")
    private boolean isDisabled() {
        return this.transferCooldown > MixinHelper.getMaxCooldown(this);
    }

}
