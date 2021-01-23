package com.kevun1.solpotato.item.foodcontainer;

import com.kevun1.solpotato.tracking.FoodList;
import com.kevun1.solpotato.tracking.FoodTracker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class FoodContainerItem extends Item {
    private String displayName;
    private int nslots;

    public FoodContainerItem(int nslots, String displayName) {
        super(new Properties().group(ItemGroup.MISC).maxStackSize(1).setNoRepair());

        this.displayName = displayName;
        this.nslots = nslots;
    }

    @Override
    public boolean isFood() {
        return true;
    }

    @Override
    public Food getFood() {
        return new Food.Builder().build();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote && player.isCrouching()) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new FoodContainerProvider(displayName), player.getPosition());
        }

        if (!player.isCrouching()) {
            return processRightClick(world, player, hand);
        }
        return ActionResult.resultPass(player.getHeldItem(hand));
    }

    private ActionResult<ItemStack> processRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (isInventoryEmpty(stack)) {
            return ActionResult.resultPass(stack);
        }

        if (player.canEat(false)) {
            player.setActiveHand(hand);
            return ActionResult.resultConsume(stack);
        }
        return ActionResult.resultFail(stack);
    }

    private static boolean isInventoryEmpty(ItemStack container) {
        ItemStackHandler handler = getInventory(container);
        if (handler == null) {
            return true;
        }

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.isFood()) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new FoodContainerCapabilityProvider(stack, nslots);
    }

    @Nullable
    public static ItemStackHandler getInventory(ItemStack bag) {
        if (bag.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
            return (ItemStackHandler) bag.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().get();
        return null;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return stack;
        }

        PlayerEntity player = (PlayerEntity) entity;
        ItemStackHandler handler = getInventory(stack);
        if (handler == null) {
            return stack;
        }

        int bestFoodSlot = getBestFoodSlot(handler, player);
        if (bestFoodSlot < 0) {
            return stack;
        }

        ItemStack bestFood = handler.getStackInSlot(bestFoodSlot);
        if (bestFood.isFood() && !bestFood.isEmpty()) {
            ItemStack result = bestFood.onItemUseFinish(world, entity);
            // put bowls/bottles etc. into player inventory
            if (!result.isFood()) {
                handler.setStackInSlot(bestFoodSlot, ItemStack.EMPTY);
                PlayerEntity playerEntity = (PlayerEntity) entity;

                if (!playerEntity.inventory.addItemStackToInventory(result)) {
                    playerEntity.dropItem(result, false);
                }
            }

            if (!world.isRemote) {
                FoodTracker.updateFoodList(bestFood.getItem(), player);
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack){
        return 32;
    }

    public static int getBestFoodSlot(ItemStackHandler handler, PlayerEntity player) {
        FoodList foodList = FoodList.get(player);

        double maxDiversity = -Double.MAX_VALUE;
        int bestFoodSlot = -1;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack food = handler.getStackInSlot(i);

            if (!food.isFood() || food.isEmpty())
                continue;
            double diversityChange = foodList.simulateFoodAdd(food.getItem());
            if (diversityChange > maxDiversity) {
                maxDiversity = diversityChange;
                bestFoodSlot = i;
            }
        }

        return bestFoodSlot;
    }
}
