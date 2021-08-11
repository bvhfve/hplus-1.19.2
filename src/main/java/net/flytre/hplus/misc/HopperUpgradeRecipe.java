package net.flytre.hplus.misc;


import net.flytre.hplus.HplusInit;
import net.flytre.hplus.filter.HopperUpgrade;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class HopperUpgradeRecipe extends SpecialCraftingRecipe {


    private static final Ingredient HOPPER_PLUS;
    private static final Ingredient UPGRADE;

    static {
        HOPPER_PLUS = Ingredient.ofItems(Items.HOPPER);
        UPGRADE = Ingredient.ofItems(HopperUpgrade.UPGRADES.toArray(new HopperUpgrade[0]));
    }

    public HopperUpgradeRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        int upgrade = -1;
        ItemStack hopper = ItemStack.EMPTY;
        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemStack = inv.getStack(j);
            if (!itemStack.isEmpty()) {
                if (UPGRADE.test(itemStack)) {
                    if (upgrade == -1)
                        upgrade = j;
                    else
                        return false;
                }
                if (HOPPER_PLUS.test(itemStack)) {
                    if (hopper.isEmpty())
                        hopper = itemStack.copy();
                    else
                        return false;
                }
            }
        }
        if (upgrade != -1 && !(hopper.isEmpty())) {
            DefaultedList<ItemStack> stack = HopperItem.getUpgrades(hopper);
            if (stack == null)
                return true;
            ItemStack upgradeStack = inv.getStack(upgrade);
            boolean empty = false;
            for (ItemStack i : stack) {
                if (upgradeStack.getItem() == i.getItem())
                    return false;
                if (i.isEmpty())
                    empty = true;
            }
            return empty;
        }
        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack hopper = ItemStack.EMPTY;
        ItemStack upgrade = ItemStack.EMPTY;


        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemStack = inv.getStack(j);
            if (!itemStack.isEmpty()) {
                if (UPGRADE.test(itemStack)) {
                    if (upgrade.isEmpty())
                        upgrade = itemStack.copy();
                }
                if (HOPPER_PLUS.test(itemStack)) {
                    if (hopper.isEmpty())
                        hopper = itemStack.copy();
                }
            }
        }

        DefaultedList<ItemStack> list = HopperItem.getUpgrades(hopper);
        if (list == null)
            list = DefaultedList.ofSize(9, ItemStack.EMPTY);
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isEmpty()) {
                index = i;
                break;
            }
        }
        ItemStack singleUpgrade = upgrade.copy();
        singleUpgrade.setCount(1);
        list.set(index, singleUpgrade);
        return HopperItem.getWithUpgradeData(list);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public ItemStack getOutput() {
        return new ItemStack(Items.HOPPER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HplusInit.UPGRADE_RECIPE;
    }
}
