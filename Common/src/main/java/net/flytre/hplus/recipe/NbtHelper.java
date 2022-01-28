package net.flytre.hplus.recipe;

import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class NbtHelper {

    public static ItemStack getWithUpgradeData(DefaultedList<ItemStack> inv, String tagName, Item item) {
        NbtCompound upgrades = new NbtCompound();
        InventoryUtils.writeNbt(upgrades, inv, "Upgrades");
        NbtCompound base = new NbtCompound();
        base.put(tagName, upgrades);
        ItemStack result = new ItemStack(item);
        result.setNbt(base);
        return result;
    }

    public static DefaultedList<ItemStack> getUpgrades(ItemStack stack, String tagName) {
        NbtCompound tag = stack.getSubNbt(tagName);

        if (tag == null)
            return null;

        DefaultedList<ItemStack> result = DefaultedList.ofSize(4, ItemStack.EMPTY);
        InventoryUtils.readNbt(tag, result, "Upgrades");

        return result;
    }
}
