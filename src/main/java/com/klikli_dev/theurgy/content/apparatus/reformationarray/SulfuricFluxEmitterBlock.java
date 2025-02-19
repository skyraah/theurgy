// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.theurgy.content.apparatus.reformationarray;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.klikli_dev.theurgy.content.behaviour.selection.SelectionBehaviour;
import com.klikli_dev.theurgy.registry.BlockEntityRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SulfuricFluxEmitterBlock extends DirectionalBlock implements EntityBlock {

    public static final MapCodec<SulfuricFluxEmitterBlock> CODEC = simpleCodec(SulfuricFluxEmitterBlock::new);

    private static final int SHAPE_LENGTH = 4;
    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(
            ImmutableMap.<Direction, VoxelShape>builder()
                    .put(Direction.EAST, Block.box(0, 4, 4, SHAPE_LENGTH, 12, 12))
                    .put(Direction.WEST, Block.box(16 - SHAPE_LENGTH, 4, 4, 16, 12, 12))
                    .put(Direction.NORTH, Block.box(4, 4, 16 - SHAPE_LENGTH, 12, 12, 16))
                    .put(Direction.SOUTH, Block.box(4, 4, 0, 12, 12, SHAPE_LENGTH))
                    .put(Direction.UP, Block.box(4, 0, 4, 12, SHAPE_LENGTH, 12))
                    .put(Direction.DOWN, Block.box(4, 16 - SHAPE_LENGTH, 4, 12, 16, 12))
                    .build()
    );

    protected SelectionBehaviour<SulfuricFluxEmitterSelectedPoint> selectionBehaviour;
    protected SulfuricFluxEmitterInteractionBehaviour interactionBehaviour;

    public SulfuricFluxEmitterBlock(Properties pProperties) {
        super(pProperties);

        this.selectionBehaviour = new SulfuricFluxEmitterSelectionBehaviour();
        this.interactionBehaviour = new SulfuricFluxEmitterInteractionBehaviour();

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    public SelectionBehaviour<SulfuricFluxEmitterSelectedPoint> selectionBehaviour() {
        return this.selectionBehaviour;
    }

    public SulfuricFluxEmitterInteractionBehaviour interactionBehaviour() {
        return this.interactionBehaviour;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        return this.interactionBehaviour.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES.get(pState.getValue(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getClickedFace();
        BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos().relative(direction.getOpposite()));
        return blockstate.is(this) && blockstate.getValue(FACING) == direction ?
                this.defaultBlockState().setValue(FACING, direction.getOpposite()) : this.defaultBlockState().setValue(FACING, direction);
    }


    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);

        if (!this.canSurvive(pState, pLevel, pPos)) {
            pLevel.destroyBlock(pPos, true);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        var facing = pState.getValue(FACING);
        var facingNeighborState = pLevel.getBlockState(pPos.relative(facing.getOpposite()));
        return facingNeighborState.isFaceSturdy(pLevel, pPos, facing);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockEntityRegistry.SULFURIC_FLUX_EMITTER.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof SulfuricFluxEmitterBlockEntity blockEntity) {
                    blockEntity.tickClient();
                }
            };
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof SulfuricFluxEmitterBlockEntity blockEntity) {
                blockEntity.tickServer();
            }
        };
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }
}
