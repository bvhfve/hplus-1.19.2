package net.flytre.hplus.recipe;

import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.flytre.hplus.Registry;
import net.flytre.hplus.filter.HopperUpgrade;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class HopperMinecartRecipe extends SpecialCraftingRecipe {

    private static final Ingredient MINECART;
    private static final Ingredient HOPPER;

    static {
        MINECART = Ingredient.ofItems(Items.MINECART);
        HOPPER = Ingredient.ofItems(Items.HOPPER);
    }

    public HopperMinecartRecipe(Identifier id) {
        super(id);
    }

    /**
     * Matches whenever there is exactly 1 hopper and 1 minecart in the grid
     */
    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ItemStack hopper = ItemStack.EMPTY;
        ItemStack minecart = ItemStack.EMPTY;
        for (int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getStack(j);
            if (!stack.isEmpty()) {
                if (HOPPER.test(stack)) {
                    if (hopper.isEmpty())
                        hopper = stack;
                    else
                        return false;
                } else if (MINECART.test(stack)) {
                    if (minecart.isEmpty())
                        minecart = stack.copy();
                    else
                        return false;
                } else
                    return false;
            }
        }
        DefaultedList<ItemStack> upgrades = NbtHelper.getUpgrades(hopper, "BlockEntityTag");
        return !hopper.isEmpty()
                && upgrades != null
                && !upgrades.stream().allMatch(ItemStack::isEmpty)
                && !minecart.isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack hopper = ItemStack.EMPTY;
        for (int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getStack(j);
            if (!stack.isEmpty() && HOPPER.test(stack))
                hopper = stack;
        }
        DefaultedList<ItemStack> list = NbtHelper.getUpgrades(hopper, "BlockEntityTag");
        return NbtHelper.getWithUpgradeData(list, "EntityTag", Items.HOPPER_MINECART);
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Items.HOPPER_MINECART);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registry.HOPPER_MINECART_RECIPE;
    }
}
