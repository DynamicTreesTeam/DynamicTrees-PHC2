package maxhyper.dtphc2.blocks;

import com.dtteam.dynamictrees.tree.TreeHelper;
import com.dtteam.dynamictrees.tree.species.Species;
import com.mojang.serialization.MapCodec;
import maxhyper.dtphc2.init.DTPHC2Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MapleSpileBucketBlock extends MapleSpileCommon {
    public static final MapCodec<MapleSpileBucketBlock> CODEC = simpleCodec(MapleSpileBucketBlock::new);

    static VoxelShape makeBucketShape() {
        VoxelShape bucket = Shapes.join(Block.box(4, 0, 1, 12, 9, 9), Block.box(5, 1, 2, 11, 9, 8), BooleanOp.ONLY_FIRST);
        return Shapes.join(bucket, makeShape(), BooleanOp.OR);
    }

    public MapleSpileBucketBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(FILLING, 0));
        SHAPE_N = rotateShape(Direction.SOUTH, Direction.NORTH, makeBucketShape());
        SHAPE_E = rotateShape(Direction.SOUTH, Direction.EAST, SHAPE_N);
        SHAPE_S = rotateShape(Direction.WEST, Direction.SOUTH, SHAPE_N);
        SHAPE_W = rotateShape(Direction.WEST, Direction.WEST, SHAPE_N);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, FILLING));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        Direction dir = state.getValue(FACING);
        if (state.hasProperty(FILLING)) {
            if (level.getBlockState(pos).getValue(FILLING) == 0 && player.isCrouching()) {
                level.setBlock(pos, DTPHC2Blocks.MAPLE_SPILE_BLOCK.get().defaultBlockState().setValue(FACING, dir), 3);
                player.addItem(new ItemStack(Items.BUCKET));
                return InteractionResult.SUCCESS;
            }
        }
        if (giveSyrup(level, pos, state, player, pos.offset(dir.getOpposite().getNormal()))) {
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected boolean giveSyrup(Level world, BlockPos pos, BlockState state, Player player, BlockPos treePos) {
        Species species = TreeHelper.getExactSpecies(world, treePos);
        if (species == Species.NULL_SPECIES) return false;
        int filling = world.getBlockState(pos).getValue(FILLING);
        if (filling > 0) {
            if (!world.isClientSide() && !world.restoringBlockSnapshots) {
                int count = (filling + (filling == maxFilling ? 1 : 0)); //Adds one bonus syrup if collected when its full
                ItemStack drop = new ItemStack(getSyrupItem(species), count);
                world.addFreshEntity(new ItemEntity(world, pos.getX()+0.5f, pos.getY()+0.1f, pos.getZ()+0.5f, drop));
            }
            world.playSound(null, pos, SoundEvents.HONEY_DRINK, SoundSource.BLOCKS, 1, 2 - filling / 3f);
            if (filling == maxFilling) world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.5f, 0.8f);
            world.setBlock(pos, state.setValue(FILLING, 0), 3);
            return true;
        }
        return false;
    }

    @Override
    public Item asItem() {
        return Items.BUCKET;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return pBlockState.getValue(FILLING);
    }

}
