package maxhyper.dtphc2.genfeatures;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.configuration.ConfigurationProperty;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeature;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.ferreusveritas.dynamictrees.systems.genfeature.context.PostGenerationContext;
import maxhyper.dtphc2.blocks.FruitVineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class VinesInTrunkGenFeature extends GenFeature {

    protected final BooleanProperty[] sideVineStates = new BooleanProperty[]{null, null, VineBlock.NORTH, VineBlock.SOUTH, VineBlock.WEST, VineBlock.EAST};

    public static final ConfigurationProperty<Block> BLOCK = ConfigurationProperty.block("block");
    public static final ConfigurationProperty<Float> PLACE_FRUIT_CHANCE = ConfigurationProperty.floatProperty("place_fruit_chance");

    public VinesInTrunkGenFeature(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected void registerProperties() {
        this.register(BLOCK, PLACE_CHANCE, MAX_HEIGHT, PLACE_FRUIT_CHANCE);
    }

    @Override
    public GenFeatureConfiguration createDefaultConfiguration() {
        return new GenFeatureConfiguration(this)
                .with(BLOCK, Blocks.VINE)
                .with(PLACE_CHANCE, 0.8f)
                .with(MAX_HEIGHT, 10)
                .with(PLACE_FRUIT_CHANCE, 0.1f);
    }

    @Override
    protected boolean postGenerate(GenFeatureConfiguration configuration, PostGenerationContext context) {
        boolean placed = false;
        for (Direction dir : Direction.Plane.HORIZONTAL){
            if (context.random().nextFloat() < configuration.get(PLACE_CHANCE)){
                BlockPos offset = context.pos().offset(dir.getNormal());
                placeVines(configuration, context.level(), offset, dir);
                placed = true;
            }
        }
        return placed;
    }

    private void placeVines (GenFeatureConfiguration configuration, LevelAccessor world, BlockPos pos, Direction direction){
        BlockState state = configuration.get(BLOCK).defaultBlockState().setValue(sideVineStates[direction.getOpposite().ordinal()], true);
        RandomSource rand = world.getRandom();
        for (int i=0; i < configuration.get(MAX_HEIGHT); i++){
            BlockPos current = pos.above(i);
            if (VineBlock.isAcceptableNeighbour(world, current.above(), Direction.UP) || TreeHelper.isBranch(world.getBlockState(current.above())))
                state = state.setValue(VineBlock.UP, true);
            if (state.hasProperty(FruitVineBlock.ageProperty) && rand.nextFloat() < configuration.get(PLACE_FRUIT_CHANCE))
                state = state.setValue(FruitVineBlock.ageProperty, 1 + rand.nextInt(FruitVineBlock.maxAge - 1));
            if (world.getBlockState(current).canBeReplaced() && TreeHelper.isBranch(world.getBlockState(current.offset(direction.getOpposite().getNormal())))){
                world.setBlock(current, state, 0);
            }
        }
    }

}