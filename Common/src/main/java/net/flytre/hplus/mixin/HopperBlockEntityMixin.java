package net.flytre.hplus.mixin;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.filter.HopperUpgrade;
import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;


/**
 * Makes hoppers implement UpgradeInventory
 * Makes hopper entity logic account for upward hoppers
 */
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends LootableContainerBlockEntity implements Hopper, UpgradeInventory {


    @Unique
    private static final VoxelShape REVERSE_INSIDE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0);
    @Unique
    private static final VoxelShape BELOW_SHAPE = Block.createCuboidShape(0.0, -16.0, 0.0, 16.0, 0.0, 16.0);
    @Unique
    private static final VoxelShape REVERSE_INPUT_AREA_SHAPE = VoxelShapes.union(BELOW_SHAPE, REVERSE_INSIDE_SHAPE);

    @Unique
    private DefaultedList<ItemStack> upgrades;

    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }


    @ModifyVariable(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isInventoryEmpty(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/util/math/Direction;)Z"))
    private static Direction hplus$extractDirection(Direction direction, World world, Hopper hopper) {
        return MixinHelper.getExtractDirection(hopper);
    }

    @ModifyConstant(method = "getInputInventory", constant = @Constant(doubleValue = 1.0))
    private static double hplus$getInputInventory(double constant, World world, Hopper hopper) {
        return MixinHelper.getExtractDirection(hopper) == Direction.UP ? -1 : 1;
    }

    @Redirect(method = "getInputItemEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/Hopper;getInputAreaShape()Lnet/minecraft/util/shape/VoxelShape;"))
    private static VoxelShape hplus$upwardHopperItemSucking(Hopper instance) {
        return MixinHelper.getExtractDirection(instance) == Direction.UP ? REVERSE_INPUT_AREA_SHAPE : instance.getInputAreaShape();
    }

    @Override
    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    public void flytre_lib$init(BlockPos pos, BlockState state, CallbackInfo ci) {
        upgrades = DefaultedList.ofSize(5, ItemStack.EMPTY);
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

    /**
     * O(1) has upgrade
     */
    @Override
    public boolean hasUpgrade(Item upgrade) {
        return upgrades.stream().map(ItemStack::getItem).anyMatch(i -> i.equals(upgrade));
    }

    @Override
    public void markUpgradesDirty() {
        if (this.world != null) {
            markDirty(this.world, this.pos, this.getCachedState());
        }
    }
}
