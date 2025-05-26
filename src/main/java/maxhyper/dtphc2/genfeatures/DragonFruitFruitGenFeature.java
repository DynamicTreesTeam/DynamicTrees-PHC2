package maxhyper.dtphc2.genfeatures;

import com.dtteam.dynamictrees.api.worldgen.LevelContext;
import com.dtteam.dynamictrees.block.fruit.Fruit;
import com.dtteam.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.dtteam.dynamictrees.systems.season.SeasonHelper;
import com.dtteam.dynamictrees.utility.CoordUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;

public class DragonFruitFruitGenFeature extends BananaFruitGenFeature {

    public DragonFruitFruitGenFeature(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected void addFruit(GenFeatureConfiguration configuration, LevelContext world, BlockPos rootPos, BlockPos leavesPos, boolean worldGen) {
        if (rootPos.getY() == leavesPos.getY()) return;
        LevelAccessor acc = world.accessor();
        CoordUtils.Surround placeDir = CoordUtils.Surround.values()[acc.getRandom().nextInt(8)];
        BlockPos pos = expandRandom(configuration, acc, leavesPos.offset(placeDir.getOffset()));
        if (acc.getBlockState(pos).canBeReplaced()) {
            Float seasonValue = SeasonHelper.getSeasonValue(world, rootPos);
            Fruit fruit = configuration.get(FRUIT);
            if (worldGen) {
                fruit.placeDuringWorldGen(acc, pos, seasonValue);
            } else {
                fruit.place(acc, pos, seasonValue);
            }
        }
    }

}