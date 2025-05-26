package maxhyper.dtphc2.genfeatures;

import com.dtteam.dynamictrees.api.configuration.ConfigurationProperty;
import com.dtteam.dynamictrees.api.worldgen.LevelContext;
import com.dtteam.dynamictrees.block.pod.Pod;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.dtteam.dynamictrees.systems.genfeature.context.PostGenerationContext;
import com.dtteam.dynamictrees.systems.genfeature.context.PostGrowContext;
import com.dtteam.dynamictrees.systems.season.SeasonHelper;
import com.dtteam.dynamictrees.tree.TreeHelper;
import com.dtteam.dynamictrees.utility.CoordUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;

public class PalmFruitGenFeature extends GenFeature {

    public static final ConfigurationProperty<Pod> POD =
            ConfigurationProperty.property("pod", Pod.class);

    public static final ConfigurationProperty<Integer> EXPAND_UP_FRUIT_HEIGHT = ConfigurationProperty.integer("expand_up_fruit_height");
    public static final ConfigurationProperty<Integer> EXPAND_DOWN_FRUIT_HEIGHT = ConfigurationProperty.integer("expand_down_fruit_height");

        /*

     -v-      -v-      -v-
    / v \    /ovo\    / v \
     oIo      oIo      oIo
      I        I       oIo
      I        I        I
      I        I        I
      I        I        I

     */

    public PalmFruitGenFeature(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected void registerProperties() {
        this.register(POD, QUANTITY, FRUITING_RADIUS, PLACE_CHANCE, EXPAND_UP_FRUIT_HEIGHT, EXPAND_DOWN_FRUIT_HEIGHT);
    }

    @Override
    public GenFeatureConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration()
                .with(POD, Pod.NULL)
                .with(QUANTITY, 8)
                .with(FRUITING_RADIUS, 6)
                .with(PLACE_CHANCE, 0.25f)
                .with(EXPAND_UP_FRUIT_HEIGHT, 0)
                .with(EXPAND_DOWN_FRUIT_HEIGHT, 0);
    }

    @Override
    protected boolean postGrow(GenFeatureConfiguration configuration, PostGrowContext context) {
        final LevelAccessor world = context.level();
        final BlockPos rootPos = context.pos();
        if ((TreeHelper.getRadius(world, rootPos.above()) >= configuration.get(FRUITING_RADIUS)) && context.natural() &&
                world.getRandom().nextInt() % 16 == 0) {
            if (context.species().seasonalFruitProductionFactor(LevelContext.create(world), rootPos) > world.getRandom().nextFloat()) {
                addFruit(configuration, context.levelContext(), rootPos, getLeavesHeight(rootPos, world).below(), false);
                return true;
            }
        }
        return false;
    }

    private BlockPos getLeavesHeight(BlockPos rootPos, LevelAccessor world) {
        for (int y = 1; y < 20; y++) {
            BlockPos testPos = rootPos.above(y);
            if ((world.getBlockState(testPos).getBlock() instanceof LeavesBlock)) {
                return testPos;
            }
        }
        return rootPos;
    }

    @Override
    protected boolean postGenerate(GenFeatureConfiguration configuration, PostGenerationContext context) {
        final LevelAccessor world = context.level();
        final BlockPos rootPos = context.pos();
        boolean placed = false;
        int qty = configuration.get(QUANTITY);
        qty *= context.fruitProductionFactor();
        for (int i = 0; i < qty; i++) {
            if (!context.endPoints().isEmpty() && world.getRandom().nextFloat() <= configuration.get(PLACE_CHANCE)) {
                addFruit(configuration, context.levelContext(), rootPos, context.endPoints().get(0), true);
                placed = true;
            }
        }
        return placed;
    }

    protected void addFruit(GenFeatureConfiguration configuration, LevelContext context, BlockPos rootPos, BlockPos leavesPos, boolean worldGen) {
        if (rootPos.getY() == leavesPos.getY()) return;
        LevelAccessor world = context.accessor();
        Direction placeDir = CoordUtils.HORIZONTALS[world.getRandom().nextInt(4)];
        BlockPos pos = expandRandom(configuration, world, leavesPos.offset(placeDir.getNormal()));
        if (world.getBlockState(pos).canBeReplaced()) {
            Float seasonValue = SeasonHelper.getSeasonValue(context, rootPos);
            Pod pod = configuration.get(POD);
            if (worldGen) {
                pod.placeDuringWorldGen(world, pos, seasonValue, placeDir.getOpposite(), 8);
            } else {
                pod.place(world, pos, seasonValue, placeDir.getOpposite(), 8);
            }
        }
    }

    protected BlockPos expandRandom(GenFeatureConfiguration configuration, LevelAccessor world, BlockPos startingPos){
        int fullHeight = 1+configuration.get(EXPAND_UP_FRUIT_HEIGHT)+configuration.get(EXPAND_DOWN_FRUIT_HEIGHT);
        return startingPos.below(configuration.get(EXPAND_DOWN_FRUIT_HEIGHT))
                .above(world.getRandom().nextInt(fullHeight));
    }

}