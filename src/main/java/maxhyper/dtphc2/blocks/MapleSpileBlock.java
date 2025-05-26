package maxhyper.dtphc2.blocks;

import com.dtteam.dynamictrees.tree.TreeHelper;
import com.dtteam.dynamictrees.tree.species.Species;
import com.mojang.serialization.MapCodec;
import maxhyper.dtphc2.init.DTPHC2Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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

public class MapleSpileBlock extends MapleSpileCommon {
    public static final MapCodec<MapleSpileBlock> CODEC = simpleCodec(MapleSpileBlock::new);

    public MapleSpileBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FILLED, false));
        SHAPE_N = rotateShape(Direction.SOUTH, Direction.NORTH, makeShape());
        SHAPE_E = rotateShape(Direction.SOUTH, Direction.EAST, SHAPE_N);
        SHAPE_S = rotateShape(Direction.WEST, Direction.SOUTH, SHAPE_N);
        SHAPE_W = rotateShape(Direction.WEST, Direction.WEST, SHAPE_N);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, FILLED));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.hasProperty(FILLED) && player.getItemInHand(hand).getItem() == Items.BUCKET) {
            Direction dir = state.getValue(FACING);
            level.setBlock(pos, DTPHC2Blocks.MAPLE_SPILE_BUCKET_BLOCK.get().defaultBlockState()
                    .setValue(FACING, dir)
                    .setValue(MapleSpileBucketBlock.FILLING, state.getValue(FILLED) ? 1 : 0), 3);
            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
            level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 1, 1f);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.hasProperty(FILLED)) {
            Direction dir = state.getValue(FACING);
            if (giveSyrup(level, pos, state, player, pos.offset(dir.getOpposite().getNormal()))) {
//                if (world.random.nextFloat() <= chanceToBreak) {
//                    world.destroyBlock(pos, true);
//                    world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BREAK, SoundCategory.BLOCKS, 1, 1, false);
//                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected boolean giveSyrup(Level world, BlockPos pos, BlockState state, Player player, BlockPos treePos) {
        Species species = TreeHelper.getExactSpecies(world, treePos);
        if (species == Species.NULL_SPECIES) return false;
        if (state.getValue(FILLED)) {
            if (!world.isClientSide() && !world.restoringBlockSnapshots)
                world.addFreshEntity(new ItemEntity(world, pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f, new ItemStack(getSyrupItem(species))));
            world.playSound(null, pos, SoundEvents.HONEY_DRINK, SoundSource.BLOCKS, 1, 2f);
            world.setBlock(pos, state.setValue(FILLED, false), 3);
            return true;
        }
        return false;
    }

    @Override
    public Item asItem() {
        return Items.IRON_INGOT;
    }

}
