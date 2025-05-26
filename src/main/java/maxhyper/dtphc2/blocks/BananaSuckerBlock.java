package maxhyper.dtphc2.blocks;

import com.dtteam.dynamictrees.item.Seed;
import com.dtteam.dynamictrees.tree.species.Species;
import com.mojang.serialization.MapCodec;
import maxhyper.dtphc2.DynamicTreesPHC2;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Optional;

public class BananaSuckerBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<BananaSuckerBlock> CODEC = simpleCodec(BananaSuckerBlock::new);

    protected static final VoxelShape SHAPE_E = Shapes.box(
            0 /16f,0  /16f,6  /16f,
            4 /16f,15 /16f,10 /16f);
    protected static final VoxelShape SHAPE_W = Shapes.box(
            12  /16f,0 /16f, 6  /16f,
            16 /16f,15 /16f,10 /16f);
    protected static final VoxelShape SHAPE_N = Shapes.box(
            6  /16f,0  /16f,12  /16f,
            10 /16f,15 /16f,16 /16f);
    protected static final VoxelShape SHAPE_S = Shapes.box(
            6  /16f,0  /16f,0 /16f,
            10 /16f,15 /16f,4 /16f);

    public BananaSuckerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState belowBlockState = level.getBlockState(blockpos);
        net.neoforged.neoforge.common.util.TriState soilDecision = belowBlockState.canSustainPlant(level, blockpos, Direction.UP, state);
        if (!soilDecision.isDefault()) return soilDecision.isTrue();
        return this.mayPlaceOn(belowBlockState, level, blockpos);
    }

    @Override
    @Nonnull
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_E;
            case SOUTH -> SHAPE_S;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    private Item seed;

    @Override
    public @NotNull Item asItem() {
        if (seed == null) {
            Optional<Seed> fetch = Species.REGISTRY.get(DynamicTreesPHC2.location("banana")).getSeed();
            if (fetch.isPresent()) seed = fetch.get();
            else seed = Items.AIR;
        }
        return seed;
    }

}
