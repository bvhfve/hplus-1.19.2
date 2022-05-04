package net.flytre.hplus.mixin;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.filter.HopperUpgrade;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;


/**
 * Makes hopper minecarts implement UpgradeInventory
 */
@Mixin(HopperMinecartEntity.class)
public abstract class HopperMinecartEntityMixin extends StorageMinecartEntity implements UpgradeInventory {
    private DefaultedList<ItemStack> upgrades;

    protected HopperMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDD)V", at = @At("TAIL"))
    public void hplus$init(World world, double x, double y, double z, CallbackInfo ci) {
        upgrades = DefaultedList.ofSize(5, ItemStack.EMPTY);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
    public void  hplus$init(EntityType<? extends HopperMinecartEntity> entityType, World world, CallbackInfo ci) {
        upgrades = DefaultedList.ofSize(5, ItemStack.EMPTY);
    }

    @Override
    public Set<Item> validUpgrades() {
        return HopperUpgrade.UPGRADES;
    }


    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void hplus$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        UpgradeInventory.toTag(nbt, upgrades);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void hplus$readNbt(NbtCompound nbt, CallbackInfo ci) {
        UpgradeInventory.fromTag(nbt, upgrades);
    }

    @Redirect(method = "dropItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/HopperMinecartEntity;dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;"))
    public ItemEntity hplus$dropStackedHopper(HopperMinecartEntity instance, ItemConvertible itemConvertible) {
        ItemStack stack = new ItemStack(Items.HOPPER);

        NbtCompound tag = stack.getOrCreateSubNbt("BlockEntityTag");
        UpgradeInventory.toTag(tag, upgrades);
        return dropStack(stack);
    }

}
