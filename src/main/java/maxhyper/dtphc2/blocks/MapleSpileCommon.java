package maxhyper.dtphc2.blocks;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.growthlogic.context.DirectionSelectionContext;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import com.google.common.collect.ImmutableMap;
import maxhyper.dtphc2.genfeatures.DTPHC2GenFeatures;
import maxhyper.dtphc2.genfeatures.SyrupGenFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public abstract class MapleSpileCommon extends HorizontalDirectionalBlock {

    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final int maxFilling = 3;
    public static final IntegerProperty FILLING = IntegerProperty.create("filling", 0, maxFilling);

    protected VoxelShape SHAPE_N;
    protected VoxelShape SHAPE_E;
    protected VoxelShape SHAPE_S;
    protected VoxelShape SHAPE_W;

    protected static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Block.box(7, 10, -1, 9, 12, 4), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(7, 10, 4, 9, 11, 6), BooleanOp.OR);
        return shape;
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        int times = (to.ordinal() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                System.out.println("min: "+ maxZ+", "+minX+" | max: "+ minZ+ ", "+ maxX);
                buffer[1] = Shapes.or(buffer[1], Shapes.box(1- maxZ, minY, minX, 1- minZ, maxY, maxX));
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public MapleSpileCommon() {
        super(Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.5f).randomTicks());
    }

    protected abstract boolean giveSyrup(Level world, BlockPos pos, BlockState state, Player player, BlockPos treePos);

    public static Item getSyrupItem(Species species) {
        if (species == null) return Items.AIR;
        GenFeatureConfiguration featureConfig = species.getGenFeatures().stream().filter(fc -> fc.getGenFeature() == DTPHC2GenFeatures.SYRUP_GEN).findFirst().orElse(null);
        if (featureConfig == null) return Items.AIR;
        return ((SyrupGenFeature) DTPHC2GenFeatures.SYRUP_GEN).getSyrupItem(featureConfig);
    }

    //For now we cannot get the exact species, so only the common species' tint can be used
    public int colorMultiplier(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (tintIndex != 0)  return 1;
        Species species = null;
        BlockPos treePos = pos.offset(state.getValue(FACING).getOpposite().getNormal());
        BlockState treeState = level.getBlockState(treePos);
        if (treeState.getBlock() instanceof BranchBlock branch) {
            species = branch.getFamily().getCommonSpecies();
        }
        if (species == null) return 1;

        GenFeatureConfiguration featureConfig = species.getGenFeatures().stream().filter(fc -> fc.getGenFeature() == DTPHC2GenFeatures.SYRUP_GEN).findFirst().orElse(null);
        if (featureConfig == null) return 0xFF00FF; //Bright purple to show that the genfeature is missing
        return ((SyrupGenFeature) DTPHC2GenFeatures.SYRUP_GEN).getTint(featureConfig);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = this.defaultBlockState();
        Direction direction = pContext.getClickedFace();
        if (direction.getAxis().isHorizontal())
            return blockstate.setValue(FACING, direction);
        return blockstate;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos offsetPos = pos.relative(state.getValue(FACING).getOpposite());
        BlockState offsetState = world.getBlockState(offsetPos);
        return TreeHelper.isBranch(offsetState) && TreeHelper.getRadius(world, offsetPos) >= 7;
    }

    @Override
    @Nonnull
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pFacing == pState.getValue(FACING).getOpposite() && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_E;
            case SOUTH -> SHAPE_S;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Nonnull
    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Nonnull
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

}
