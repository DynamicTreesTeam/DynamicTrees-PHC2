package maxhyper.dtphc2.genfeatures;

import com.dtteam.dynamictrees.api.registry.Registry;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import maxhyper.dtphc2.DynamicTreesPHC2;

public class DTPHC2GenFeatures {

    public static final GenFeature BANANA_FRUIT = new BananaFruitGenFeature(DynamicTreesPHC2.location("banana_fruit"));
    public static final GenFeature DRAGON_FRUIT_FRUIT = new DragonFruitFruitGenFeature(DynamicTreesPHC2.location("dragon_fruit_fruit"));
    public static final GenFeature PALM_FRUIT = new PalmFruitGenFeature(DynamicTreesPHC2.location("palm_fruit"));
    public static final GenFeature SYRUP_GEN = new SyrupGenFeature(DynamicTreesPHC2.location("syrup"));
    public static final GenFeature PLANT_SUCKERS = new PlantSuckerGenFeature(DynamicTreesPHC2.location("plant_suckers"));
    public static final GenFeature TRUNK_VINES = new VinesInTrunkGenFeature(DynamicTreesPHC2.location("trunk_vines"));

    public static void register(final Registry<GenFeature> registry) {
        registry.registerAll(BANANA_FRUIT, DRAGON_FRUIT_FRUIT, PALM_FRUIT, SYRUP_GEN, PLANT_SUCKERS, TRUNK_VINES);
    }

}
