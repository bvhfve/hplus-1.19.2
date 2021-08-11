package net.flytre.hplus.misc;

import net.flytre.flytre_lib.api.storage.inventory.filter.FilterInventory;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.HplusInit;
import net.flytre.hplus.filter.FilterUpgrade;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MixinHelper {

    private MixinHelper() {
        throw new AssertionError("");
    }

    public static boolean hasUpgrade(Object hopper, Item upgrade) {
        return hopper instanceof UpgradeInventory && ((UpgradeInventory) hopper).hasUpgrade(upgrade);
    }


    public static int getMaxCooldown(Object hopper) {
        int multi = hopper instanceof StoneHopperEntity ? 3 : 1;
        if (hasUpgrade(hopper, HplusInit.SPEED_UPGRADE_HIGH))
            return 2 * multi;
        if (hasUpgrade(hopper, HplusInit.SPEED_UPGRADE))
            return 4 * multi;
        return 8 * multi;
    }

    public static boolean passFilterTest(Object hopper, ItemStack stack) {
        if (!(hopper instanceof UpgradeInventory))
            return true;

        UpgradeInventory inv = (UpgradeInventory) hopper;
        ItemStack filter = null;

        for (ItemStack i : inv.getUpgrades())
            if (i.getItem() instanceof FilterUpgrade) {
                filter = i;
                break;
            }

        if (filter == null)
            return true;
        FilterInventory filterInventory = FilterUpgrade.getInventory(filter);
        return filterInventory.passFilterTest(stack);
    }

}
