package net.flytre.hplus.mixin;


import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StorageMinecartEntity.class)
public interface StorageMinecartEntityAccessor {

    @Accessor("inventory")
    DefaultedList<ItemStack> getInvStackList();
}
