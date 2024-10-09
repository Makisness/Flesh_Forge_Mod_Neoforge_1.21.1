package net.makisness.fleshforgemod.block.custom;

import com.mojang.serialization.MapCodec;
import net.makisness.fleshforgemod.block.entity.FleshForgeBlockEntity;
import net.makisness.fleshforgemod.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.ClientHooks;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class FleshForgeBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0,0,0,16,16,16);

    public FleshForgeBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()){
            BlockEntity blockEntity =level.getBlockEntity(pos);
            if(blockEntity instanceof FleshForgeBlockEntity){
                ((FleshForgeBlockEntity) blockEntity).drops();
            }
        }

            super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(!level.isClientSide()) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof FleshForgeBlockEntity){
            MenuProvider containerProvider = (MenuProvider) entity;

            if(player instanceof ServerPlayer serverPlayer){
                serverPlayer.openMenu(containerProvider);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()){
            return null;
        }
        return createTickerHelper(blockEntityType, ModBlockEntities.FLESH_FORGE_BE.get(),
                ((level1, pos, state1, blockEntity) -> blockEntity.tick(level1,pos,state1)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FleshForgeBlockEntity(pos,state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }
}
