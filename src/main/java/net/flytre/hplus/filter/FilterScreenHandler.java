package net.flytre.hplus.filter;


import net.flytre.flytre_lib.api.storage.inventory.filter.FilterInventory;
import net.flytre.hplus.HplusInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.Set;

public class FilterScreenHandler extends ScreenHandler {
    private final FilterInventory inventory;
    private final PlayerInventory playerInventory;
    private final int inventoryHeight = 3;

    public FilterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new FilterInventory(DefaultedList.ofSize(27, ItemStack.EMPTY), 0, 3, false, false));
    }

    public FilterScreenHandler(final int syncId, final PlayerInventory playerInventory, final Inventory inventory) {
        super(HplusInit.FILTER_SCREEN_HANDLER, syncId);
        this.inventory = (FilterInventory) inventory;
        this.playerInventory = playerInventory;
        checkSize(inventory, 9 * inventoryHeight);
        inventory.onOpen(playerInventory.player);
        setupSlots();
    }

    public FilterInventory getInventory() {
        return inventory;
    }

    public void setupSlots() {
        int i = (this.inventoryHeight - 4) * 18;


        int n;
        int m;
        for (n = 0; n < this.inventoryHeight; ++n) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inventory, m + n * 9, 8 + m * 18, 18 + n * 18));
            }
        }

        for (n = 0; n < 3; ++n) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 102 + n * 18 + i));
            }
        }

        for (n = 0; n < 9; ++n) {
            this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 160 + i));
        }
    }

    @Override
    public void onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        if (slotId >= 0) {
            ItemStack stack = getSlot(slotId).getStack();
            boolean isPlayerInventory = slotId >= inventory.size();
            if (!isPlayerInventory) {
                inventory.removeStack(slotId);
            } else {
                if (!inventory.containsAny(Set.of(stack.getItem())))
                    inventory.put(stack);
            }

            getSlot(slotId).inventory.markDirty();
        }
    }

    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        inventory.onClose(player);
        ItemStack stack = player.getOffHandStack();
        if (!(stack.getItem() instanceof FilterUpgrade))
            stack = player.getMainHandStack();
        int filter = stack.getOrCreateSubNbt("filter").getInt("type");
        boolean modMatch = stack.getOrCreateSubNbt("filter").getBoolean("modMatch");
        boolean nbtMatch = stack.getOrCreateSubNbt("filter").getBoolean("nbtMatch");
        stack.getOrCreateNbt().put("filter", inventory.writeNbt());
        stack.getOrCreateSubNbt("filter").putInt("type", filter);
        stack.getOrCreateSubNbt("filter").putBoolean("modMatch", modMatch);
        stack.getOrCreateSubNbt("filter").putBoolean("nbtMatch", nbtMatch);

    }

    @Override
    public boolean canUse(final PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        return false;
    }

}