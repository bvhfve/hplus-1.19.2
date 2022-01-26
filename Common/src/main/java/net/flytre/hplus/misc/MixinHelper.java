package net.flytre.hplus.misc;

import net.flytre.flytre_lib.api.storage.inventory.filter.FilterInventory;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.Registry;
import net.flytre.hplus.filter.FilterUpgrade;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class MixinHelper {

    private MixinHelper() {
        throw new AssertionError("");
    }

    public static boolean hasUpgrade(Object hopper, Item upgrade) {
        return hopper instanceof UpgradeInventory && ((UpgradeInventory) hopper).hasUpgrade(upgrade);
    }


    public static int getMaxCooldown(Object hopper) {
        int multi = hopper instanceof StoneHopperEntity ? 3 : 1;
        if (hasUpgrade(hopper, Registry.SPEED_UPGRADE_HIGH))
            return 2 * multi;
        if (hasUpgrade(hopper, Registry.SPEED_UPGRADE))
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


    public static Direction getExtractDirection(Hopper hopper) {

        if (hopper instanceof BlockEntity) {
            BlockState state = ((BlockEntity) hopper).getCachedState();
            if (state.getProperties().contains(HopperBlock.FACING)) {
                return ((BlockEntity) hopper).getCachedState().get(HopperBlock.FACING) == Direction.UP ? Direction.UP : Direction.DOWN;
            }
        }

        return Direction.DOWN;
    }

}
