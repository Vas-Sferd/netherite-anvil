package net.gamenet.netherite_anvil.item;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.gamenet.netherite_anvil.menu.NetheriteAnvilMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetheriteAnvil extends FallingBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape COUNTERPART = Block.box(0.0, 7.0, 2.0, 16.0, 14.0, 14.0);
    private static final VoxelShape LEFT_DETAIL = Block.box(3.0, 8.0, 14.0, 13.0, 13.0, 15.0);
    private static final VoxelShape RIGHT_DETAIL = Block.box(3.0, 8.0, 1.0, 13.0, 13.0, 2.0);
    private static final VoxelShape STRENGTH = Block.box(2.0, 5.0, 3.0, 14.0, 7.0, 13.0);
    private static final VoxelShape BOTTOM = Block.box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0);
    private static final VoxelShape FOOT = Block.box(4.0, 1.0, 4.0, 12.0, 2.0, 12.0);
    private static final VoxelShape TOWER = Block.box(4.0, 2.0, 5.0, 12.0, 5.0, 11.0);
    private static final VoxelShape COUNTERPART_ROTATED = Block.box(2.0, 7.0, 0.0, 14.0, 14.0, 16.0);
    private static final VoxelShape LEFT_DETAIL_ROTATED = Block.box(1.0, 8.0, 3.0, 2.0, 13.0, 13.0);
    private static final VoxelShape RIGHT_DETAIL_ROTATED = Block.box(14.0, 8.0, 3.0, 15.0, 13.0, 13.0);
    private static final VoxelShape STRENGTH_ROTATED = Block.box(3.0, 5.0, 2.0, 13.0, 7.0, 14.0);
    private static final VoxelShape BOTTOM_ROTATED = Block.box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0);
    private static final VoxelShape FOOT_ROTATED = Block.box(4.0, 1.0, 4.0, 12.0, 2.0, 12.0);
    private static final VoxelShape TOWER_ROTATED = Block.box(5.0, 2.0, 4.0, 11.0, 5.0, 12.0);
    private static final VoxelShape SHAPE = Shapes.or(
            COUNTERPART,
            LEFT_DETAIL,
            RIGHT_DETAIL,
            STRENGTH,
            BOTTOM,
            FOOT,
            TOWER
    );
    private static final VoxelShape SHAPE_ROTATED = Shapes.or(
            COUNTERPART_ROTATED,
            LEFT_DETAIL_ROTATED,
            RIGHT_DETAIL_ROTATED,
            STRENGTH_ROTATED,
            BOTTOM_ROTATED,
            FOOT_ROTATED,
            TOWER_ROTATED
    );

    private static final Component CONTAINER_TITLE = Component.translatable("container.repair");
    private static final float FALL_DAMAGE_PER_DISTANCE = 5.0f;
    private static final int FALL_DAMAGE_MAX = 150;

    public NetheriteAnvil(FabricBlockSettings settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getClockWise());
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        player.openMenu(blockState.getMenuProvider(level, blockPos));
        player.awardStat(Stats.INTERACT_WITH_ANVIL);
        return InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
        return new SimpleMenuProvider((i, inventory, player) -> new NetheriteAnvilMenu(i, inventory, ContainerLevelAccess.create(level, blockPos)), CONTAINER_TITLE);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        Direction direction = blockState.getValue(FACING);
        if (direction.getAxis() == Direction.Axis.X) {
            return SHAPE_ROTATED;
        }
        return SHAPE;
    }

    @Override
    protected void falling(FallingBlockEntity fallingBlockEntity) {
        fallingBlockEntity.setHurtsEntities(FALL_DAMAGE_PER_DISTANCE, FALL_DAMAGE_MAX);
    }

    @Override
    public void onLand(Level level, BlockPos blockPos, BlockState blockState, BlockState blockState2, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            level.levelEvent(1031, blockPos, 0);
        }
    }

    @Override
    public void onBrokenAfterFall(Level level, BlockPos blockPos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            level.levelEvent(1029, blockPos, 0);
        }
    }

    @Override
    public @NotNull DamageSource getFallDamageSource() {
        return DamageSource.ANVIL;
    }

    @Override
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getMapColor((BlockGetter)blockGetter, (BlockPos)blockPos).col;
    }
}
