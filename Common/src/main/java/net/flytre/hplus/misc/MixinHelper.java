package net.flytre.hplus.misc;

import net.flytre.flytre_lib.api.storage.inventory.filter.FilterInventory;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.Registry;
import net.flytre.hplus.filter.FilterUpgrade;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import static net.flytre.hplus.misc.StaticConstants.FROM;

public class MixinHelper {

    private MixinHelper() {
        throw new AssertionError("");
    }

    public static boolean hasUpgrade(Object hopper, Item upgrade) {
        return hopper instanceof UpgradeInventory && ((UpgradeInventory) hopper).hasUpgrade(upgrade);
    }


    public static int getMaxCooldown(Object hopper) {
        int multi = hopper instanceof StoneHopperEntity ? 3 : 1;
        if (hasUpgrade(hopper, Registry.SPEED_UPGRADE_HIGH.get()))
            return 2 * multi;
        if (hasUpgrade(hopper, Registry.SPEED_UPGRADE.get()))
            return 4 * multi;
        return 8 * multi;
    }

    public static boolean passFilterTest(Object hopper, ItemStack stack) {
        if (!(hopper instanceof UpgradeInventory inv))
            return true;

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


    /**
     * Returns the direction this hopper extracts from.
     * For the default hopper, it extracts from an inventory above it
     * so the default value is Direction.UP.
     */
    public static Direction getExtractDirection(Hopper hopper) {

        if (hopper instanceof BlockEntity entity) {
            return entity.getCachedState().get(FROM);
        }

        return Direction.UP;
    }


}
