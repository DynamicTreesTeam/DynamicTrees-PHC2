package maxhyper.dtphc2.blocks;

import maxhyper.dtphc2.init.DTPHC2Registries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class MapleSpileBucketBlock extends MapleSpileBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final int maxFilling = 3;
    public static final IntegerProperty FILLING = IntegerProperty.create("filling", 0, maxFilling);

    private static VoxelShape makeShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0, 0.0625, 0.75, 0.5625, 0.5625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.6875, 0.0625, 0.125, 0.3125, 0.5625, 0.5), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.5625, 0.25, 0.3125, 0.84375, 0.3125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.6875, 0.5625, 0.25, 0.75, 0.84375, 0.3125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.3125, 0.78125, 0.25, 0.6875, 0.84375, 0.3125), IBooleanFunction.OR);

        return shape;
    }

    private static final VoxelShape SHAPE = makeShape();

    public MapleSpileBucketBlock() {
        super();
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FILLING, 0));
        //registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(FILLING, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILLING);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.canBlockStay(world, pos, state)) {
            this.dropBlock(world, pos, state);
        }
    }

    @Nonnull
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (state.hasProperty(FILLING)) {
            if (world.getBlockState(pos).getValue(FILLING) == 0 && player.isCrouching()) {
                Direction dir = state.getValue(FACING);
                world.setBlock(pos, DTPHC2Registries.MAPLE_SPILE_BLOCK.defaultBlockState().setValue(FACING, dir), 3);
                player.addItem(new ItemStack(Items.BUCKET));
                return ActionResultType.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    protected boolean giveSyrup(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        int filling = worldIn.getBlockState(pos).getValue(FILLING);
        if (filling > 0) {
            if (worldIn.isClientSide() && !worldIn.restoringBlockSnapshots) {
                //TODO: make dynamic
                ResourceLocation mapleSyrupRes = new ResourceLocation("pamhc2trees", "maplesyrupitem");
                Item mapleSyrup = ForgeRegistries.ITEMS.getValue(mapleSyrupRes);
                //ItemStack drop = new ItemStack(FruitRegistry.getLog(FruitRegistry.MAPLE).getFruitItem());
                int count = (filling + (filling == maxFilling ? 1 : 0)); //Adds one bonus syrup if collected when its full
                ItemStack drop = new ItemStack(mapleSyrup, count);
                player.addItem(drop);
            }
            worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.BLOCKS, 1, 1 + filling / 4f, false);
            worldIn.setBlock(pos, state.setValue(FILLING, 0), 3);
            return true;
        }
        return false;
    }

    @Override
    protected void dropBlock(World worldIn, BlockPos pos, BlockState state) {
        worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BUCKET)));
        worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_NUGGET)));
    }

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity pTe, ItemStack stack) {
        if (!player.isCreative()) {
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BUCKET)));
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_NUGGET)));
        }
        super.playerDestroy(worldIn, player, pos, state, pTe, stack);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(Items.BUCKET);
    }

    //TODO: Maybe reimplement these methods idk
//    @Override
//    public boolean hasComparatorInputOverride(BlockState state) {
//        return true;
//    }
//
//    @Override
//    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
//        return blockState.getValue(FILLING) * 5;
//    }
//
//    @Override
//    public BlockRenderLayer getBlockLayer() {
//        return BlockRenderLayer.CUTOUT_MIPPED;
//    }

    @Override
    @Nonnull
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

}