package net.flytre.hplus.mixin;


import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootableContainerBlockEntity.class)
public interface LootableContainerBlockEntityInvoker {

    @Invoker("getInvStackList")
    DefaultedList<ItemStack> flytre_lib$getInventoryContents();
}
