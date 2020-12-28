/**
 * Much of the following code was adapted from Cyclic's storage bag code.
 * Copyright for portions of the code are held by Samson Basset (Lothrazar)
 * as part of Cyclic, under the MIT license.
 */
package com.kevun1.solpotato.item.foodcontainer;

import com.kevun1.solpotato.client.ContainerScreenRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public class FoodContainer extends Container {
    private int startInv;
    private int endInv;
    public static final int PLAYERSIZE = 4 * 9;

    public ItemStack containerItem;
    public int slot;
    public int nslots;

    private PlayerEntity playerEntity;
    private PlayerInventory playerInventory;

    public FoodContainer(int id, PlayerInventory playerInventory, PlayerEntity player) {
        super(ContainerScreenRegistry.food_container, id);

        if (player.getHeldItemMainhand().getItem() instanceof FoodContainerItem) {
            containerItem = player.getHeldItemMainhand();
            slot = player.inventory.currentItem;
        }
        else if (player.getHeldItemOffhand().getItem() instanceof FoodContainerItem) {
            containerItem = player.getHeldItemOffhand();
            slot = 40;
        }
        else {
            for (int x = 0; x < playerInventory.getSizeInventory(); x++) {
                ItemStack stack = playerInventory.getStackInSlot(x);
                if (stack.getItem() instanceof FoodContainerItem) {
                    containerItem = stack;
                    slot = x;
                    break;
                }
            }
        }

        this.playerEntity = player;
        this.playerInventory = playerInventory;
        containerItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            nslots = h.getSlots();
            this.endInv = h.getSlots();
            for (int j = 0; j < h.getSlots(); j++) {
                int row = j / 9;
                int col = j % 9;
                int xPos = 8 + col * 18;
                int yPos = 8 + row * 18;
                this.addSlot(new FoodSlot(h, j, xPos, yPos));
            }
        });

        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        try {
            //if last machine slot is 17, endInv is 18
            int playerStart = endInv;
            int playerEnd = endInv + PLAYERSIZE;//53 = 17 + 36
            //standard logic based on start/end
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                itemstack = stack.copy();
                if (index < this.endInv) {
                    if (!this.mergeItemStack(stack, playerStart, playerEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index <= playerEnd && !this.mergeItemStack(stack, startInv, endInv, false)) {
                    return ItemStack.EMPTY;
                }
                if (stack.isEmpty()) {
                    slot.putStack(ItemStack.EMPTY);
                }
                else {
                    slot.onSlotChanged();
                }
                if (stack.getCount() == itemstack.getCount()) {
                    return ItemStack.EMPTY;
                }
                slot.onTake(playerIn, stack);
            }
            return itemstack;
        }
        catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        if (!(slotId < 0 || slotId >= this.inventorySlots.size())) {
            ItemStack myBag = this.inventorySlots.get(slotId).getStack();
            if (myBag.getItem() instanceof FoodContainerItem) {
                //lock the bag in place by returning empty
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    private int addSlotRange(PlayerInventory handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(PlayerInventory handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}
