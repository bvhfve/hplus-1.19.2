package net.flytre.hplus2.hopper;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface HopperPlus {

    int getMaxCooldown();

    boolean hasVacuumUpgrade();

    boolean hasTrashUpgrade();

    boolean hasLockedUpgrade();

    int getFirstEmptyUpgradeSlot();

    DefaultedList<ItemStack> getUpgrades();

    boolean passFilterTest(ItemStack stack);

    boolean hasUpgrade(ItemStack stack);

    void onUpgradesUpdated();
}

