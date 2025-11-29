package maxhyper.dtphc2.genfeatures;

import com.dtteam.dynamictrees.api.configuration.ConfigurationProperty;
import com.dtteam.dynamictrees.api.network.MapSignal;
import com.dtteam.dynamictrees.api.worldgen.LevelContext;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.dtteam.dynamictrees.systems.genfeature.context.PostGrowContext;
import com.dtteam.dynamictrees.systems.season.SeasonHelper;
import com.dtteam.dynamictrees.tree.TreeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nonnull;
import java.awt.*;

public class SyrupGenFeature extends GenFeature {

    private static final ConfigurationProperty<Float> BASE_SYRUP_CHANCE = ConfigurationProperty.floatProperty("base_syrup_chance");
    private static final ConfigurationProperty<Float> OUT_OF_SEASON_SYRUP_CHANCE = ConfigurationProperty.floatProperty("out_of_season_syrup_chance");
    public static final ConfigurationProperty<Float> SEASONAL_OFFSET = ConfigurationProperty.floatProperty("seasonal_offset");
    private static final ConfigurationProperty<Item> SYRUP_ITEM = ConfigurationProperty.item("syrup_item");
    private static final ConfigurationProperty<String> TINT = ConfigurationProperty.string("tint");

    public SyrupGenFeature(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected void registerProperties() {
        this.register(BASE_SYRUP_CHANCE, OUT_OF_SEASON_SYRUP_CHANCE, SYRUP_ITEM, SEASONAL_OFFSET, TINT);
    }

    @Nonnull
    @Override
    public GenFeatureConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration()
                .with(BASE_SYRUP_CHANCE, 0.05F)
                .with(OUT_OF_SEASON_SYRUP_CHANCE, 0.001F)
                .with(SYRUP_ITEM, Items.AIR)
                .with(SEASONAL_OFFSET, 3.5F)
                .with(TINT, "#FF00FF");//ugly purple to show that its missing
    }

    public Item getSyrupItem(GenFeatureConfiguration config){
        return config.get(SYRUP_ITEM);
    }

    public int getTint(GenFeatureConfiguration config) { return Color.decode(config.get(TINT)).getRGB(); }

    @Override
    public boolean postGrow(@Nonnull GenFeatureConfiguration configuration, @Nonnull PostGrowContext context) {
        LevelContext levelContext = context.levelContext();
        Level level = context.levelContext().level();
        boolean natural = context.natural();
        final BlockPos rootPos = context.pos();
        if (natural && (TreeHelper.getRadius(level, context.treePos()) >= 7) && (level.getRandom().nextFloat() <= getSyrupChance(levelContext, rootPos, configuration))) {
            dripSyrup(level, rootPos);
        }
        return true;
    }

    //Update syrup extract rate depending on seasons
    public double getSyrupChance(LevelContext world, BlockPos pos, GenFeatureConfiguration config) {
        float factor = seasonalFruitProductionFactor(world, pos, config);
        return Math.max(factor * config.get(BASE_SYRUP_CHANCE), config.get(OUT_OF_SEASON_SYRUP_CHANCE));
    }

    private void dripSyrup(LevelAccessor world, BlockPos rootPos) {
        TreeHelper.startAnalysisFromRoot(world, rootPos, new MapSignal(new DripSyrupNode()));
    }

    public float seasonalFruitProductionFactor(LevelContext levelContext, BlockPos pos, GenFeatureConfiguration config) {
        Float season = SeasonHelper.getSeasonValue(levelContext, pos);
        if (config.getAsOptional(SEASONAL_OFFSET).isEmpty() || season == null) return 1.0f;

        return SeasonHelper.globalSeasonalFruitProductionFactor(
                levelContext,
                new BlockPos(0,(int)(season*64),-1),
                -config.get(SEASONAL_OFFSET),
                true);
    }

}