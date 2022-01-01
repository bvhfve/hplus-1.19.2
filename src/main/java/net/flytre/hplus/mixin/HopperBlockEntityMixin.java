package net.flytre.hplus.mixin;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.filter.HopperUpgrade;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;


/**
 * Makes hoppers implement UpgradeInventory
 * Makes hopper entity logic account for upward hoppers
 */
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends LootableContainerBlockEntity implements Hopper, UpgradeInventory {

    private DefaultedList<ItemStack> upgrades;
    private Set<Item> upgradeCache;

    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    private static Direction getExtractDirection(Hopper hopper) {

        if (hopper instanceof BlockEntity) {
            BlockState state = ((BlockEntity) hopper).getCachedState();
            if (state.getProperties().contains(HopperBlock.FACING)) {
                return ((BlockEntity) hopper).getCachedState().get(HopperBlock.FACING) == Direction.UP ? Direction.UP : Direction.DOWN;
            }
        }

        return Direction.DOWN;
    }

    @ModifyVariable(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isInventoryEmpty(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/util/math/Direction;)Z"))
    private static Direction hplus$extractDirection(Direction direction, World world, Hopper hopper) {
        return getExtractDirection(hopper);
    }

    @ModifyConstant(method = "getInputInventory", constant = @Constant(doubleValue = 1.0))
    private static double hplus$getInputInventory(double constant, World world, Hopper hopper) {
        return getExtractDirection(hopper) == Direction.UP ? -1 : 1;
    }

    @Override
    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    public void flytre_lib$init(BlockPos pos, BlockState state, CallbackInfo ci) {
        upgrades = DefaultedList.ofSize(5, ItemStack.EMPTY);
        upgradeCache = new HashSet<>();
        markUpgradesDirty();
    }

    @Override
    public Set<Item> validUpgrades() {
        return HopperUpgrade.UPGRADES;
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    public void flytre_lib$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        UpgradeInventory.toTag(nbt, upgrades);
    }

    @Inject(method = "readNbt", at = @At("RETURN"))
    public void flytre_lib$readNbt(NbtCompound nbt, CallbackInfo ci) {
        UpgradeInventory.fromTag(nbt, upgrades);
        markUpgradesDirty();
    }

    @Override
    public boolean isValidUpgrade(ItemStack stack) {
        return validUpgrades().contains(stack.getItem()) && !hasUpgrade(stack.getItem());
    }

    @Override
    public void markUpgradesDirty() {
        upgradeCache.clear();
        for (ItemStack stack : upgrades)
            upgradeCache.add(stack.getItem());
    }

    /**
     * O(1) has upgrade
     */
    @Override
    public boolean hasUpgrade(Item upgrade) {
        return upgradeCache.contains(upgrade);
    }
}
