// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.theurgy.content.behaviour.interaction;

import com.klikli_dev.theurgy.content.behaviour.crafting.HasCraftingBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;


public abstract class GenericVatInteractionBehaviour<R extends Recipe<?>> implements InteractionBehaviour {
    @Override
    public ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pHand != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        var blockEntity = pLevel.getBlockEntity(pPos);

        if (!(blockEntity instanceof HasCraftingBehaviour<?, ?, ?>))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        @SuppressWarnings("unchecked") var vat = (HasCraftingBehaviour<?, R, ?>) blockEntity;

        //interaction with shift and empty hand opens/closes the vat
        if (!pPlayer.isShiftKeyDown() || !pPlayer.getMainHandItem().isEmpty()) {

            //if the vat is closed then other interactions are not allowed and we say that, and handle the event to avoid further interaction
            if (!pState.getValue(BlockStateProperties.OPEN)) {
                this.showClosedMessage(pLevel, pPlayer);
                return ItemInteractionResult.FAIL;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (pLevel.isClientSide)
            return ItemInteractionResult.SUCCESS;

        var craftingBehaviour = vat.craftingBehaviour();

        var isOpen = pState.getValue(BlockStateProperties.OPEN);

        if (isOpen) {
            //we can only close if we have a valid recipe and can craft it
            var recipe = craftingBehaviour.getRecipe();
            if (recipe.isPresent() && craftingBehaviour.canCraft(recipe.get())) {
                pLevel.setBlock(pPos, pState.setValue(BlockStateProperties.OPEN, false), Block.UPDATE_CLIENTS);
            } else {
                this.showNoRecipeMessage(pLevel, pPlayer);
                return ItemInteractionResult.FAIL;
            }
        } else {
            //when opening we stop processing (because we want to interrupt the crafting process)
            //we also set changed so that gets saved.
            pLevel.setBlock(pPos, pState.setValue(BlockStateProperties.OPEN, true), Block.UPDATE_CLIENTS);
            craftingBehaviour.stopProcessing();
            blockEntity.setChanged();
        }

        return ItemInteractionResult.SUCCESS;
    }


    protected abstract void showNoRecipeMessage(Level level, Player player);

    protected abstract void showClosedMessage(Level level, Player player);
}
