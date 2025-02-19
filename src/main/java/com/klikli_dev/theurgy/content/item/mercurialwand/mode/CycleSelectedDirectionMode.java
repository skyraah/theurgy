// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.theurgy.content.item.mercurialwand.mode;

import com.klikli_dev.theurgy.TheurgyConstants;
import com.klikli_dev.theurgy.content.item.mode.TargetDirectionSetter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CycleSelectedDirectionMode extends MercurialWandItemMode {

    private final Lazy<CycleSelectedDirectionModeRenderHandler> renderHandler;

    protected CycleSelectedDirectionMode() {
        super();
        //We're using a lazy so that on the serverside we don't construct the client-only renderer UNLESS code actually calls it in which case we will crash which is fine as a warning.
        this.renderHandler = Lazy.of(() -> new CycleSelectedDirectionModeRenderHandler(this));
    }

    @Override
    public String descriptionId() {
        return TheurgyConstants.I18n.Item.Mode.MERCURIAL_WAND_CYCLE_SELECTED_DIRECTION;
    }

    @Override
    public void appendHUDText(Player pPlayer, HitResult pHitResult, ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents) {
        var description = this.description(pStack, pLevel);
        if (pHitResult instanceof BlockHitResult blockHitResult) {
            var blockEntity = pLevel.getBlockEntity(blockHitResult.getBlockPos());
            if (blockEntity instanceof TargetDirectionSetter directionSettable) {
                var currentDirection = directionSettable.targetDirection();
                var newDirection = this.nextDirection(currentDirection);

                var component = Component.translatable(TheurgyConstants.I18n.Item.Mode.MERCURIAL_WAND_CYCLE_SELECTED_DIRECTION_WITH_TARGET,
                        Component.translatable(currentDirection.getName()).withStyle(currentDirection != newDirection ? ChatFormatting.YELLOW : ChatFormatting.GREEN),
                        Component.translatable(newDirection.getName()).withStyle(ChatFormatting.GREEN)
                );

                description = component;
            }
        }
        pTooltipComponents.add(description);
    }

    @Override
    public CycleSelectedDirectionModeRenderHandler renderHandler() {
        return this.renderHandler.get();
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        //get the target block and move its direction to the next one

        var blockPos = context.getClickedPos();
        var level = context.getLevel();

        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof TargetDirectionSetter directionSettable) {
            if (!level.isClientSide) {
                var currentDirection = directionSettable.targetDirection();
                var newDirection = this.nextDirection(currentDirection);
                directionSettable.targetDirection(newDirection);

                context.getPlayer().displayClientMessage(Component.translatable(TheurgyConstants.I18n.Item.Mode.MERCURIAL_WAND_CYCLE_SELECTED_DIRECTION_SUCCESS,
                        Component.translatable(newDirection.getName()).withStyle(ChatFormatting.GREEN)
                ), true);
            }
            return InteractionResult.SUCCESS;
        }

        return super.onItemUseFirst(stack, context);
    }

    protected Direction nextDirection(Direction direction) {
        return switch (direction) {
            case UP -> Direction.NORTH;
            case NORTH -> Direction.WEST;
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.SOUTH;
            case EAST -> Direction.DOWN;
            case DOWN -> Direction.UP;
        };
    }

    protected Direction previousDirection(Direction direction) {
        return switch (direction) {
            case UP -> Direction.DOWN;
            case NORTH -> Direction.UP;
            case SOUTH -> Direction.DOWN;
            case WEST -> Direction.NORTH;
            case EAST -> Direction.SOUTH;
            case DOWN -> Direction.NORTH;
        };
    }
}
