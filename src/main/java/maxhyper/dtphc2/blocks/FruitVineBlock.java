package maxhyper.dtphc2.blocks;

import com.dtteam.dynamictrees.api.season.ClimateZoneType;
import com.dtteam.dynamictrees.api.worldgen.LevelContext;
import com.dtteam.dynamictrees.platform.Services;
import com.dtteam.dynamictrees.platform.services.IConfigHelper;
import com.dtteam.dynamictrees.systems.season.ClimateHelper;
import com.dtteam.dynamictrees.systems.season.SeasonCompatibilityHandler;
import com.dtteam.dynamictrees.systems.season.SeasonHelper;
import com.dtteam.dynamictrees.tree.TreeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.CommonHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FruitVineBlock extends VineBlock {

    public static final int maxAge = 4;

    private static final float baseFruitingChance = 0.002f;
    private static float fruitGrowChance = 0.2f;
    private static float fruitOverripenChance = 0.005f;

    private static final float vineSpreadUpChance = 0.005f;
    private static final float attemptSpread = 0.01f;

    public static final IntegerProperty ageProperty = IntegerProperty.create("age", 0, maxAge);

    private Supplier<Item> fruit;
    private Supplier<Item> overripeFruit;

    private int matureAge = maxAge;

    @Nullable
    private Float seasonOffset = 0f;

    private float flowerSeasonHoldPeriodStart = -1.5F;
    private float flowerSeasonHoldPeriodEnd = -1.0F;

    private float minProductionFactor = 0.3F;

    private int maxFruitsAround = 2;

    public FruitVineBlock(Supplier<Item> fruit, @Nullable Supplier<Item> overripeFruit) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().strength(0.2F).sound(SoundType.VINE));
        this.registerDefaultState(defaultBlockState().setValue(ageProperty, 0));
        this.fruit = fruit;
        this.overripeFruit = overripeFruit;
    }
    public FruitVineBlock(Supplier<Item> fruit) {
        this(fruit, null);
    }

    public void setAge(Level world, BlockPos pos, BlockState state, int age, boolean destroy) {
        state = state.setValue(ageProperty, age);
        //Spawn breaking particles and breaking sound
        if (destroy) world.levelEvent(2001, pos, Block.getId(state));
        world.setBlock(pos, state, 2);
    }

    public FruitVineBlock setMatureAge(int age) {
        if (age <= maxAge && age > 0)
            matureAge = age;
        return this;
    }
    public FruitVineBlock setFruitGrowChance(float chance) {
        if (chance <= 1 && chance >= 0)
            fruitGrowChance = chance;
        return this;
    }
    public void setMaxFruitsAround(int maxFruitsAround) {
        this.maxFruitsAround = maxFruitsAround;
    }
    public void setMinProductionFactor(float minProductionFactor) {
        this.minProductionFactor = minProductionFactor;
    }
    public FruitVineBlock setFruitOverripenChance(float chance) {
        if (chance <= 1 && chance >= 0)
            fruitOverripenChance = chance;
        return this;
    }
    public FruitVineBlock setFruit(Supplier<Item> stack) {
        fruit = stack;
        return this;
    }
    public FruitVineBlock setOverripeFruit(Supplier<Item> stack) {
        overripeFruit = stack;
        return this;
    }
    public FruitVineBlock setSeasonOffset(Float seasonOffset){
        this.seasonOffset = seasonOffset;
        return this;
    }

    public Float getSeasonOffset (){
        return seasonOffset;
    }

    public void setFlowerSeasonHoldPeriodStart(float flowerSeasonHoldPeriodStart) {
        this.flowerSeasonHoldPeriodStart = flowerSeasonHoldPeriodStart;
    }

    public void setFlowerSeasonHoldPeriodEnd(float flowerSeasonHoldPeriodEnd) {
        this.flowerSeasonHoldPeriodEnd = flowerSeasonHoldPeriodEnd;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ageProperty);
    }

    public void doTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        final Integer age = getAge(state);
        if (age == null) return;
        final Float season = SeasonHelper.getSeasonValue(LevelContext.create(world), pos);

        if (season != null) { // Non-Null means we are season capable.
            if (isOutOfSeason(world, pos)) {
                this.outOfSeason(world, pos, state); // Destroy the block or similar action.
                return;
            }
            if (age == 1 && isInFlowerHoldPeriod(world, pos, season)) {
                return;
            }
        }
        if (age < maxAge) {
            tryGrow(state, world, pos, random, age, season);
        }
    }

    private void tryGrow(BlockState state, Level world, BlockPos pos, RandomSource random, int age,
                         @Nullable Float season) {
        float chance = age == 0 ? getFruitingChance(world, pos)
                : ((matureAge != maxAge && age >= matureAge) ? fruitOverripenChance : fruitGrowChance);

        final boolean doGrow = random.nextFloat() < chance;
        final boolean eventGrow = CommonHooks.canCropGrow(world, pos, state, doGrow);
        // Prevent a seasons mod from canceling the growth, we handle that ourselves.
        if (season != null ? doGrow || eventGrow : eventGrow) {
            //We look for fruit blocks around. If there is more than two we cancel the fruit growth
            int fruitFoundAround = 0;
            for (Direction dir : Direction.values()) {
                Integer sideAge = getAge(world.getBlockState(pos.offset(dir.getNormal())));
                if (sideAge != null && sideAge > 0) {
                    fruitFoundAround++;
                }
            }
            if (fruitFoundAround >= maxFruitsAround) {
                //changeVineWithProperties(world, pos, getStateFromAge(0), state);
                return;
            }
            setAge(world, pos, state, age + 1, false);
            //changeVineWithProperties(worldIn, pos, getStateFromAge(age + 1), state);
            CommonHooks.fireCropGrowPost(world, pos, state);
        }
    }

    public float seasonalFruitProductionFactor(LevelContext level, BlockPos pos) {
        return seasonOffset != null ?
                SeasonHelper.globalSeasonalFruitProductionFactor(level, pos, -seasonOffset)
                : 1.0F;
    }

    private boolean isOutOfSeason(Level world, BlockPos pos) {
        return seasonalFruitProductionFactor(LevelContext.create(world), pos) < minProductionFactor;
    }

    private void outOfSeason(Level world, BlockPos pos, BlockState state) {
        world.setBlock(pos, state.setValue(ageProperty, 0), 2);
    }


    public final boolean isInFlowerHoldPeriod(Level level, BlockPos rootPos, Float seasonValue) {
        Float offset = seasonOffset;
        if (offset == null) {
            return false;
        } else {
            Float peakSeasonValue = SeasonHelper.getPeakFruitProductionSeason(LevelContext.create(level), rootPos, offset);
            if (peakSeasonValue != null && this.flowerSeasonHoldPeriodEnd != 0.0F) {
                float min = this.flowerSeasonHoldPeriodStart + peakSeasonValue;
                float max = this.flowerSeasonHoldPeriodEnd + peakSeasonValue;
                return SeasonHelper.isSeasonBetween(seasonValue, min, max);
            } else {
                return false;
            }
        }
    }

    private float getFruitingChance(Level world, BlockPos pos) {
        if (seasonOffset == null) return baseFruitingChance;
        float fruitFactor = SeasonHelper.globalSeasonalFruitProductionFactor(LevelContext.create(world), pos, seasonOffset);
        return baseFruitingChance * Math.max((fruitFactor + 0.25f), 1);
    }

    public Integer getAge(BlockState state) {
        if (!state.hasProperty(ageProperty)) return null;
        return state.getValue(ageProperty);
    }

    public int getMatureAge() {
        return matureAge;
    }

    @Nonnull
    private BlockState getStateFromAge(int age) {
        return defaultBlockState().setValue(ageProperty, age);
    }

    @Nullable
    private ItemStack getFruit() {
        if (fruit == null) return ItemStack.EMPTY;
        return new ItemStack(fruit.get());
    }

    @Nullable
    private ItemStack getOverripeFruit() {
        if (overripeFruit == null) return ItemStack.EMPTY;
        return new ItemStack(overripeFruit.get());
    }

    private boolean spawnItemFruitIfRipe(Level world, BlockPos pos, BlockState state) {
        Integer age = getAge(state);
        if (!world.isClientSide() && age != null) {
            if (age >= matureAge) {
                ItemStack fruit = (age == matureAge) ? getFruit() : getOverripeFruit();
                if (fruit == null) return false;
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, fruit));
                return true;
            }
        }

        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        Integer age = getAge(state);
        if (age == null) return InteractionResult.PASS;
        // Drop fruit if mature.
        if (age >= matureAge) {
            if (spawnItemFruitIfRipe(level, pos, state)) {
                setAge(level, pos, state, 0, true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    public static boolean isAcceptableNeighbour(BlockGetter pBlockReader, BlockPos pLevel, Direction pNeighborPos) {
        BlockState blockstate = pBlockReader.getBlockState(pLevel);
        return Block.isFaceFull(blockstate.getCollisionShape(pBlockReader, pLevel), pNeighborPos.getOpposite()) || blockstate.is(BlockTags.LEAVES) || TreeHelper.isBranch(blockstate);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        doTick(state, world, pos, random);

        if (world.random.nextFloat() < attemptSpread && world.isAreaLoaded(pos, 4)) { // Forge: check area to prevent loading unloaded chunks
            Direction randDir = Direction.getRandom(random);
            BlockPos upPos = pos.above();
            if (randDir.getAxis().isHorizontal() && !state.getValue(getPropertyForFace(randDir))) {
                //TODO
                if (this.canSpread(world, pos)) {
                    BlockPos offsetPos = pos.relative(randDir);
                    BlockState offsetState = world.getBlockState(offsetPos);
                    if (offsetState.isAir()) {
                        Direction rightDir = randDir.getClockWise();
                        Direction leftDir = randDir.getCounterClockWise();
                        boolean hasFaceRight = state.getValue(getPropertyForFace(rightDir));
                        boolean hasFaceLeft = state.getValue(getPropertyForFace(leftDir));
                        BlockPos rightPos = offsetPos.relative(rightDir);
                        BlockPos leftPos = offsetPos.relative(leftDir);
                        if (hasFaceRight && isAcceptableNeighbour(world, rightPos, rightDir)) {
                            world.setBlock(offsetPos, this.defaultBlockState().setValue(getPropertyForFace(rightDir), true), 2);
                        } else if (hasFaceLeft && isAcceptableNeighbour(world, leftPos, leftDir)) {
                            world.setBlock(offsetPos, this.defaultBlockState().setValue(getPropertyForFace(leftDir), true), 2);
                        } else {
                            Direction oppositeDir = randDir.getOpposite();
                            if (hasFaceRight && world.isEmptyBlock(rightPos) && isAcceptableNeighbour(world, pos.relative(rightDir), oppositeDir)) {
                                world.setBlock(rightPos, this.defaultBlockState().setValue(getPropertyForFace(oppositeDir), true), 2);
                            } else if (hasFaceLeft && world.isEmptyBlock(leftPos) && isAcceptableNeighbour(world, pos.relative(leftDir), oppositeDir)) {
                                world.setBlock(leftPos, this.defaultBlockState().setValue(getPropertyForFace(oppositeDir), true), 2);
                            } else if (world.random.nextFloat() < vineSpreadUpChance && isAcceptableNeighbour(world, offsetPos.above(), Direction.UP)) {
                                world.setBlock(offsetPos, this.defaultBlockState().setValue(UP, true), 2);
                            }
                        }
                    } else if (isAcceptableNeighbour(world, offsetPos, randDir)) {
                        world.setBlock(pos, state.setValue(getPropertyForFace(randDir), true), 2);
                    }

                }
            } else {
                if (randDir == Direction.UP && pos.getY() < 255) {
                    if (this.canSupportAtFace(world, pos, randDir)) {
                        world.setBlock(pos, state.setValue(UP, true), 2);
                        return;
                    }

                    if (world.isEmptyBlock(upPos)) {
                        if (!this.canSpread(world, pos)) {
                            return;
                        }

                        BlockState blockstate3 = state;

                        for(Direction direction2 : Direction.Plane.HORIZONTAL) {
                            if (random.nextBoolean() || !isAcceptableNeighbour(world, upPos.relative(direction2), Direction.UP)) {
                                blockstate3 = blockstate3.setValue(getPropertyForFace(direction2), false);
                            }
                        }

                        if (this.hasHorizontalConnection(blockstate3)) {
                            world.setBlock(upPos, blockstate3, 2);
                        }

                        return;
                    }
                }

                if (pos.getY() > 0) {
                    BlockPos blockpos1 = pos.below();
                    BlockState blockstate = world.getBlockState(blockpos1);
                    boolean isAir = blockstate.isAir();
                    if (isAir || blockstate.is(this)) {
                        BlockState blockstate1 = isAir ? this.defaultBlockState() : blockstate;
                        BlockState blockstate2 = this.copyRandomFaces(state, blockstate1, random);
                        if (blockstate1 != blockstate2 && this.hasHorizontalConnection(blockstate2)) {
                            world.setBlock(blockpos1, blockstate2, 2);
                        }
                    }
                }

            }
        }
    }

    private boolean canSpread(BlockGetter blockReader, BlockPos pos) {
        int i = 4;
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(
                pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4, pos.getX() + 4, pos.getY() + 1, pos.getZ() + 4
        );
        int j = 5;

        for (BlockPos blockpos : iterable) {
            if (blockReader.getBlockState(blockpos).is(this)) {
                if (--j <= 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasHorizontalConnection(BlockState state) {
        return state.getValue(NORTH) || state.getValue(EAST) || state.getValue(SOUTH) || state.getValue(WEST);
    }

    private BlockState copyRandomFaces(BlockState from, BlockState to, RandomSource random) {
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextBoolean()) {
                BooleanProperty booleanproperty = getPropertyForFace(direction);
                if (from.getValue(booleanproperty)) {
                    to = to.setValue(booleanproperty, Boolean.TRUE);
                }
            }
        }

        return to;
    }

}
