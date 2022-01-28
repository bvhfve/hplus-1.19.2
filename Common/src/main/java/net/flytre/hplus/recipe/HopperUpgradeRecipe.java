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

public class HopperUpgradeRecipe extends SpecialCraftingRecipe {


    private static final Ingredient HOPPER_LIKE;
    private static final Ingredient UPGRADE;

    static {
        HOPPER_LIKE = Ingredient.ofItems(Items.HOPPER, Items.HOPPER_MINECART);
        UPGRADE = Ingredient.ofItems(HopperUpgrade.UPGRADES.toArray(new Item[0]));
    }


    public HopperUpgradeRecipe(Identifier id) {
        super(id);
    }

    public static DefaultedList<ItemStack> getUpgrades(ItemStack stack) {
        if (stack.getItem() == Items.HOPPER) {
            return NbtHelper.getUpgrades(stack, "BlockEntityTag");
        } else if (stack.getItem() == Items.HOPPER_MINECART) {
            return NbtHelper.getUpgrades(stack, "EntityTag");
        }
        throw new AssertionError("Unexpected item!");
    }

    public static ItemStack getWithUpgradeData(ItemStack hopperLike, DefaultedList<ItemStack> list) {
        if (hopperLike.getItem() == Items.HOPPER) {
            return NbtHelper.getWithUpgradeData(list, "BlockEntityTag", Items.HOPPER);
        } else if (hopperLike.getItem() == Items.HOPPER_MINECART) {
            return NbtHelper.getWithUpgradeData(list, "EntityTag", Items.HOPPER_MINECART);
        }
        throw new AssertionError("Unexpected item!");
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        int upgrade = -1;
        ItemStack hopperLike = ItemStack.EMPTY;
        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemStack = inv.getStack(j);
            if (!itemStack.isEmpty()) {
                if (UPGRADE.test(itemStack)) {
                    if (upgrade == -1)
                        upgrade = j;
                    else
                        return false;
                }
                if (HOPPER_LIKE.test(itemStack)) {
                    if (hopperLike.isEmpty())
                        hopperLike = itemStack.copy();
                    else
                        return false;
                }
            }
        }
        if (upgrade != -1 && !(hopperLike.isEmpty())) {
            DefaultedList<ItemStack> stack = getUpgrades(hopperLike);
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

            int index = InventoryUtils.getFirstEmptySlot(getUpgrades(hopperLike));
            return empty && index != -1;
        }

        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack hopperLike = ItemStack.EMPTY;
        ItemStack upgrade = ItemStack.EMPTY;


        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemStack = inv.getStack(j);
            if (!itemStack.isEmpty()) {
                if (UPGRADE.test(itemStack)) {
                    if (upgrade.isEmpty())
                        upgrade = itemStack.copy();
                }
                if (HOPPER_LIKE.test(itemStack)) {
                    if (hopperLike.isEmpty())
                        hopperLike = itemStack.copy();
                }
            }
        }

        DefaultedList<ItemStack> list = getUpgrades(hopperLike);
        if (list == null)
            list = DefaultedList.ofSize(5, ItemStack.EMPTY);
        int index = InventoryUtils.getFirstEmptySlot(list);
        ItemStack singleUpgrade = upgrade.copy();
        singleUpgrade.setCount(1);
        list.set(index, singleUpgrade);
        return getWithUpgradeData(hopperLike, list);
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
        return Registry.UPGRADE_RECIPE;
    }
}
